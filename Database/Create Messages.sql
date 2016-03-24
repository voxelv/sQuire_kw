
use squiredb;

create table Messages (
	MID integer not null primary key auto_increment,
    timeSent datetime,
    fromID integer,
    channelID integer,
    messageText varchar(240),
    foreign key (fromID) references Users(userID),
    foreign key (channelID) references Channels(channelID)
);

create table Channels (
	channelID integer not null primary key auto_increment,
    channelName varchar(30)
);

create table Subscriptions (
	subID integer not null primary key auto_increment,
    channelID integer,
    userID integer,
	joinTime datetime,
    foreign key (channelID) references Channels(channelID),
    foreign key (userID) references Users(userID)
);