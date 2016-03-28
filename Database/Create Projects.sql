
use squire;

create table PFiles (
	pfid integer not null primary key auto_increment,
    pfname varchar(30),
	pid integer,
	timeCreated timestamp,
    creatorID integer,
	foreign key (pid) references Projects(PID),
    foreign key (creatorID) references Users(userID)
);

create table Projects (
	PID integer not null primary key auto_increment,
	pname varchar(30),
	location varchar(40)
);

create table ProjectAccess (
	PID integer not null primary key,
	userID integer not null primary key,
	foreign key (PID) references Projects(PID),
	foreign key (userID) references Users(userID)
);

create table FileEdits (
	edID integer not null primary key auto_increment,
	pfID integer,
	userID integer,
	timeof timestamp,
	foreign key (pfID) references PFiles(pfID),
	foreign key (userID) references Users(userID)
);