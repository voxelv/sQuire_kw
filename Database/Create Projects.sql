
use squire;

create table PFLines {
	pflid integer not null primary key auto_increment,
	nextid integer,
	text varchar(255),
	lastEditor integer,
	timeEdited timestamp,
	foreign key (lastEditor) references Users(userID),
	foreign key (nextid) references PFLines(pflid)
}

create table Projects (
	PID integer not null primary key auto_increment,
	pname varchar(30),
	location varchar(40)
);

create table PFiles (
	pfid integer not null primary key auto_increment,
    pfname varchar(30),
	pid integer,
	pflhead integer,
	timeCreated timestamp,
    creatorID integer,
	foreign key (pflhead) references PFLines(pflid),
	foreign key (pid) references Projects(PID),
    foreign key (creatorID) references Users(userID)
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

delimiter //
CREATE PROCEDURE PFLTraverser 
(
    in inputNo int
) 
BEGIN 
    declare final_id int default NULL;
    SELECT nextID 
    INTO final_id 
    FROM PFLines
    WHERE pflid = inputNo;
    IF( final_id is not null) THEN
        INSERT INTO results(SELECT * FROM PFLines WHERE pflid = inputNo);
        CALL PFLTraverser(final_id);   
    end if;
END//
delimiter ;

