/*SEQUENCES*/
create sequence if not exists public.contact_seq
    start with 1
    increment by 50;

create sequence if not exists public.address_seq
    start with 1
    increment by 50;

create sequence if not exists public.email_seq
    start with 1
    increment by 50;

create sequence if not exists public.history_seq
    start with 1
    increment by 50;

create sequence if not exists public.message_seq
    start with 1
    increment by 50;

create sequence if not exists public.telephone_seq
    start with 1
    increment by 50;

create sequence if not exists public.customer_seq
    start with 1
    increment by 50;

create sequence if not exists public.professional_seq
    start with 1
    increment by 50;

create sequence if not exists public.skill_seq
    start with 1
    increment by 1;

create sequence if not exists public.job_offer_seq
    start with 1
    increment by 50;

/*TABLES*/
create table if not exists public.customer
(
    id      bigint primary key not null default nextval('customer_seq'),
    comment varchar(255)                default null
);

create table if not exists public.professional
(
    id          bigint primary key not null default nextval('professional_seq'),
    comment          varchar(255) default null,
    location         varchar(255),
    daily_rate       double precision not null,
    employment_state varchar(100) check (employment_state
        in ('EMPLOYED', 'UNEMPLOYED_AVAILABLE', 'NOT_AVAILABLE') ) not null
);

create table if not exists public.skill
(
    id              bigint primary key not null default nextval('skill_seq'),
    skill           varchar(255),
    professional_id bigint,
    foreign key (professional_id) references public.professional (id)
);

create table if not exists public.contact
(
    id              bigint primary key               not null default nextval('contact_seq'),
    comment         varchar(255),
    category        varchar(100) check ( category
        in ('CUSTOMER', 'PROFESSIONAL', 'UNKNOWN') ) not null,
    name            varchar(255)                     not null,
    ssn_code        varchar(16),
    surname         varchar(255)                     not null,
    customer_id     bigint,
    professional_id bigint,
    foreign key (customer_id) references public.customer (id) on delete cascade on update cascade,
    foreign key (professional_id) references public.professional (id)
        on delete cascade on update cascade
);

create table if not exists public.email
(
    id         bigint primary key not null default nextval('email_seq'),
    comment    varchar(255),
    email      varchar(255),
    contact_id bigint,
    foreign key (contact_id) references public.contact (id)
);

create table if not exists public.message
(
    id       bigint primary key                              not null default nextval('message_seq'),
    comment  varchar(255),
    body     text,
    channel  varchar(100) check (
        channel in ('EMAIL', 'PHONE_CALL', 'TEXT_MESSAGE') ) not null,
    date     timestamp(6) without time zone,
    priority varchar(100),
    sender   varchar(255),
    state    varchar(100) check (
        state in ('RECEIVED', 'READ', 'DISCARDED',
                  'PROCESSING', 'DONE', 'FAILED') )          not null,
    subject  varchar(255)
);

create table if not exists public.history
(
    id          bigint primary key                  not null default nextval('history_seq'),
    comment     varchar(255),
    date        timestamp(6) without time zone,
    state       varchar(100) check (
        state in ('RECEIVED', 'READ', 'DISCARDED',
                  'PROCESSING', 'DONE', 'FAILED') ) not null,
    messages_id bigint,
    foreign key (messages_id) references public.message (id)
);

create table if not exists public.telephone
(
    id         bigint primary key not null default nextval('telephone_seq'),
    comment    varchar(255),
    telephone  varchar(255),
    contact_id bigint,
    foreign key (contact_id) references public.contact (id)
);


create table if not exists public.job_offer
(
    id              bigint  primary key not null default nextval('job_offer_seq'),
    comment         varchar(255),
    description     varchar(255),
    duration        real   not null,
    status          varchar(255)
        constraint job_offer_status_check
            check ((status)::text = ANY
        ((ARRAY ['CREATED'::character varying, 'ABORTED'::character varying, 'SELECTION_PHASE'::character varying, 'CANDIDATE_PROPOSAL'::character varying, 'CONSOLIDATED'::character varying, 'DONE'::character varying])::text[])),
    value           real   not null,
    customer_id     bigint
            references public.customer,
    professional_id bigint
            references public.professional
);

create table if not exists public.job_offer_skills
(
    job_offer_id bigint not null
            references public.job_offer,
    skill_id     bigint not null
            references public.skill,
    primary key (job_offer_id, skill_id)
);



create table if not exists public.professional_skills
(
    professional_id bigint not null
            references public.professional,
    skill_id        bigint not null

            references public.skill,
    primary key (professional_id, skill_id)
);

create table if not exists public.address
(
    id         bigint default nextval('address_seq') not null primary key,
    comment    varchar(255),
    contact_id bigint
        references public.contact(id),
    address    varchar(255) not null
);

