package app.services.teamc;

import app.entities.teamc.Poll;
import app.entities.teamc.User;
import app.entities.teamc.UserEvent;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.teamc.CommentMapper;
import app.persistence.teamc.PollMapper;
import app.persistence.teamc.UserEventMapper;

import java.util.ArrayList;
import java.util.List;

public class PollService {
    private ConnectionPool connectionPool;
    private PollMapper pollMapper;
    private CommentMapper commentMapper;
    private UserEventMapper userEventMapper;

    public PollService(ConnectionPool connectionPool) {
        this.connectionPool =connectionPool;
        this.pollMapper = new PollMapper(connectionPool);
        this.commentMapper = new CommentMapper(connectionPool);
        this.userEventMapper = new UserEventMapper(connectionPool);
    }

    // Opret afstemning. Kun ejeren af eventet må
    public int createPoll(User owner, int eventId, String question, List<String> options,
                          List<String> voterIds, String resultsVisibility) throws DatabaseException {

        // Regel 1: kun ejeren
        UserEvent event = userEventMapper.getEventById(eventId);
        if (event == null || event.getUser().getUser_id() != owner.getUser_id()) {
            throw new IllegalArgumentException("Kun ejeren af eventet kan lave en afstemning");
        }

        // Regel 2: der skal være et spørgsmål
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Skriv et spørgsmål");
        }

        // Regel 3: mindst 2 svarmuligheder (tomme felter tæller ikke)
        List<String> cleanOptions = new ArrayList<>();
        for (String option : options) {
            if (option != null && !option.isBlank()) {
                cleanOptions.add(option.trim());
            }
        }
        if (cleanOptions.size() < 2) {
            throw new IllegalArgumentException("Der skal være mindst 2 svarmuligheder");
        }

        // Regel 4: mindst én der må stemme
        if (voterIds == null || voterIds.isEmpty()) {
            throw new IllegalArgumentException("Vælg mindst én der må stemme");
        }

        // Regel 5: kun de tre lovlige værdier
        if (!List.of("OWNER", "VOTERS", "INVITED").contains(resultsVisibility)) {
            resultsVisibility = "OWNER";
        }

        // Alt er ok: gem i databasen
        int pollId = pollMapper.createPoll(eventId, question.trim(), resultsVisibility);
        for (String option : cleanOptions) {
            pollMapper.addOption(pollId, option);
        }
        for (String id : voterIds) {
            pollMapper.addVoter(pollId, Integer.parseInt(id));
        }
        return pollId;
    }

    public Poll getPoll(int pollId) throws DatabaseException {
        return pollMapper.getPollById(pollId);
    }

    public List<Poll> getPollsForEvent(int eventId) throws DatabaseException {
        return pollMapper.getPollsForEvent(eventId);
    }

    // Må brugeren stemme lige nu? (er på listen og har ikke stemt)
    public boolean canVoteNow(User user, Poll poll) throws DatabaseException {
        return pollMapper.canVote(poll.getPoll_id(), user.getUser_id())
                && !pollMapper.hasVoted(poll.getPoll_id(), user.getUser_id());
    }

    public void vote(User user, int pollId, int optionId) throws DatabaseException {
        if (!pollMapper.canVote(pollId, user.getUser_id())) {
            throw new IllegalArgumentException("Du må ikke stemme i denne afstemning");
        }
        if (pollMapper.hasVoted(pollId, user.getUser_id())) {
            throw new IllegalArgumentException("Du har allerede stemt");
        }
        pollMapper.vote(pollId, optionId, user.getUser_id());
    }

    // Må brugeren åbne afstemningerne på eventet? (ejeren eller inviteret)
    public boolean canSeeEvent(User user, int eventId) throws DatabaseException {
        UserEvent event = userEventMapper.getEventById(eventId);
        if (event == null) {
            return false;
        }
        return event.getUser().getUser_id() == user.getUser_id()
                || commentMapper.isInvited(eventId, user.getUser_id());
    }

    public UserEvent getEvent(int eventId) throws DatabaseException {
        return userEventMapper.getEventById(eventId);
    }

    // Må brugeren se resultatet?
    public boolean canSeeResults(User user, Poll poll) throws DatabaseException {
        UserEvent event = userEventMapper.getEventById(poll.getEvent_id());
        int userId = user.getUser_id();

        if (event.getUser().getUser_id() == userId) {
            return true;                                         // ejeren ser altid resultatet
        }
        switch (poll.getResults_visibility()) {
            case "VOTERS":
                return pollMapper.canVote(poll.getPoll_id(), userId);
            case "INVITED":
                return commentMapper.isInvited(poll.getEvent_id(), userId);
            default:                                             // "OWNER"
                return false;
        }
    }
}
