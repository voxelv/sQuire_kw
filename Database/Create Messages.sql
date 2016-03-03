
use squiredb;

create table Message (
	MID integer not null primary key,
    timeSent datetime,
    fromID integer,
    channelID integer,
    messageText varchar(240),
    foreign key (fromID) references User(userID),
    foreign key (channelID) references Channel(channelID)
);

create table Channel (
	channelID integer not null primary key,
    channelName varchar(30)
);

create table Subscriptions (
	subID integer not null primary key,
    channelID integer,
    userID integer,
	joinTime datetime,
    foreign key (channelID) references Channel(channelID),
    foreign key (userID) references User(userID)
);