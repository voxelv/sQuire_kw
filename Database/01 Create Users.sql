use squire;

create table Users (
	userID integer not null primary key auto_increment,
	userName varchar(30) unique
);