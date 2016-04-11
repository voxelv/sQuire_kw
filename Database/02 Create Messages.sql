
use squire;

create table Channels (
	channelID integer unsigned not null primary key auto_increment,
    channelName varchar(30) unique
);

create table Messages (
	MID integer unsigned not null primary key auto_increment,
    timeSent timestamp,
    fromID integer unsigned DEFAULT NULL,
	channelID integer unsigned DEFAULT NULL,
    messageText varchar(255),
    foreign key (fromID) references Users(userID),
    foreign key (channelID) references Channels(channelID)
);

create table Subscriptions (
    channelID integer unsigned,
    userID integer unsigned DEFAULT NULL,
	joinTime timestamp,
	primary key (channelID, userID),
    foreign key (channelID) references Channels(channelID),
    foreign key (userID) references Users(userID)
);
