use squire;

create table Projects (
	PID integer not null primary key auto_increment,
	pname varchar(30),
	location varchar(40)
);

create table PFLines (
	pflid integer not null primary key auto_increment,
	nextid integer,
	text varchar(255),
	lastEditor integer,
	timeEdited timestamp,
	foreign key (lastEditor) references Users(userID),
	foreign key (nextid) references PFLines(pflid)
);

create table PDirs (
	pdid integer not null primary key auto_increment,
	pdname varchar(30),
	pid integer,
	parentid integer,
	foreign key (pid) references Projects(PID),
	foreign key (parentid) references PDirs (parentid)	
);

create table PFiles (
	pfid integer not null primary key auto_increment,
    pfname varchar(30),
	pid integer,
	pdid integer,
	pflhead integer,
	timeCreated timestamp,
    creatorID integer,
	foreign key (pflhead) references PFLines(pflid),
	foreign key (pdid) references PDirs(pdis),
	foreign key (pid) references Projects(PID),
    foreign key (creatorID) references Users(userID)
);

create table ProjectAccess (
	PID integer not null,
	userID integer not null,
	primary key(PID, userID),
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
    CREATE TEMPORARY TABLE IF NOT EXISTS my_temp_table select * from PFLines where 1 = 0;
    INSERT INTO my_temp_table(SELECT * FROM PFLines WHERE pflid = inputNo);
	SELECT nextID 
    INTO final_id 
    FROM PFLines
    WHERE pflid = inputNo;
    WHILE ( final_id is not null) DO
    INSERT INTO my_temp_table(SELECT * FROM PFLines WHERE pflid = final_id);
    SELECT nextID 
        INTO final_id 
        FROM PFLines
        WHERE pflid = final_id;
        
    end while;
    SELECT * FROM my_temp_table;
    DROP table my_temp_table;
END//
delimiter ;

