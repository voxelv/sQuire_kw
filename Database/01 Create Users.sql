use squire;

create table Users (
	userID integer unsigned not null primary key auto_increment,
	userName varchar(30) unique,
	lastOnline timestamp
);
