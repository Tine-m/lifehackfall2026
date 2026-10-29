BEGIN;

DROP TABLE IF EXISTS public.teamf_tasks CASCADE;
CREATE TABLE IF NOT EXISTS public.teamf_tasks
(
    taskid          serial  NOT NULL,
    taskname        character varying NOT NULL,
    taskdescription character varying,
    tasksettime     integer NOT NULL,
    taskdate        date    NOT NULL,
    taskstarttime   time    NOT NULL,
    taskstopdate    date,
    taskstoptime    time,
    taskstatus      character varying,
    CONSTRAINT teamf_tasks_pkey PRIMARY KEY (taskid)
);

COMMIT;
