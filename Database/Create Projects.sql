
use squiredb;

create table PFile (
	pfid integer not null primary key,
    name varchar(30);
	project integer;
	timeCreated datetime,
    creatorID integer,
	foreign key (project) references Project(PID),
    foreign key (creatorID) references User(userID)
);

create table Project (
	PID integer not null primary key,
	name varchar(30),
	location varchar(40)
);

create table ProjectAccess (
	PID integer not null primary key,
	userID integer not null primary key
	foreign key (PID) references Project(PID),
	foreign key (userID) references User(userID)
);