insert into PFLines(nextid, text) values (NULL, "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "    }");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "        System.out.println(\"Hello, World\");");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "        // Prints \"Hello, World\" to the terminal window.");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "    public static void main(String[] args) {");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public class HelloWorld {");
insert into PFiles(pfname, pflhead) values ("Hello World.java", (select LAST_INSERT_ID());
