create table mapUser (
name varchar(20) not null,
password varchar(50),
email varchar(50),
isOnline int not null default '0',
primary key(name,email)
)
drop table mapUser;


