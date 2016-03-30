
use squire;

create table Messages (
	MID integer not null primary key auto_increment,
    timeSent timestamp,
    fromID integer,
    channelID integer,
    messageText varchar(255),
    foreign key (fromID) references Users(userID),
    foreign key (channelID) references Channels(channelID)
);

create table Channels (
	channelID integer not null primary key auto_increment,
    channelName varchar(30)
);

create table Subscriptions (
    channelID integer,
    userID integer,
	joinTime timestamp,
	primary key (channelID, userID),
    foreign key (channelID) references Channels(channelID),
    foreign key (userID) references Users(userID)
);