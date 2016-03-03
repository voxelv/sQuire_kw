
use squiredb;

create table Message (
	MID integer not null primary key,
    timeSent datetime,
    fromID integer,
    channelID integer,
    messageText varchar(240)
);

create table Channel (
	channelID integer not null primary key,
    channelName varchar(30)
);

create table Subscriptions (
	subID integer not null primary key,
    channelID integer,
    userID integer,
	joinTime datetime
);