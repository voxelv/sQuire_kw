
use squiredb;

create table Message (
	MID integer not null primary key auto_increment,
    timeSent datetime,
    fromID integer,
    channelID integer,
    messageText varchar(240),
    foreign key (fromID) references User(userID),
    foreign key (channelID) references Channel(channelID)
);

create table Channel (
	channelID integer not null primary key auto_increment,
    channelName varchar(30)
);

create table Subscriptions (
	subID integer not null primary key auto_increment,
    channelID integer,
    userID integer,
	joinTime datetime,
    foreign key (channelID) references Channel(channelID),
    foreign key (userID) references User(userID)
);