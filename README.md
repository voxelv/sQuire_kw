# sQuire by Knight Writers
CS383 Project by the Knight Writers group.

##Proposed Git Workflow
####  This is a suggested workflow that slip5295 learned during an internship. You can do these using the git shell.
####  (If you prefer the GUI, do the GUI equivalents.)
1. Ensure you are not currently working on something (i.e. you have already committed and pushed any previous work).
2. Do `git checkout master` and then `git pull` to ensure you have all the latest changes.
3. Do `git checkout -b <name1234_change>` using your uidaho name1234 and an extremely brief description of what you plan to change. Example: `git checkout -b slip5295_editor`. This creates a new branch for you to work on, based on the latest master.
4. Work on your branch.
5. To push your branch, (This is not going to affect the master branch) do `git push -u origin <name1234_change>` where `<name1234_change>` is your branch name. Your branch will be available online for others to checkout if they wish. This branch is not the master branch.
6. You want to get the latest changes in master into your branch. Do `git fetch` and then `git rebase origin/master`.
7. If you do the above step and want to push your branch again, you must do `git push -u origin :<name1234_change>` and then `git push -u origin <name1234_change>` immediately afterward. The difference is the colon in front of the branch name. This effectively deletes the branch on github, then reuploads your new changes.
8. To merge your branch to master, you must do `git fetch`. Then, while on your branch, do `git rebase origin/master`. This effectively puts everyone else's changes in front of yours, as if they had done them before you did. This may cause conflicts that you must resolve in each file that both you and another person changed, but the important thing is you are resolving them on your branch, not master.
9. Once all conflicts are resolved, do `git checkout master`, `git merge <name1234_change>`, `git push`. This puts your changes on master.
10. To delete a merged branch, `git branch -d <name1234_change>`, `git push -u origin :<name1234_change>` (note the colon);

##OneNote
* [OneNote] (https://onedrive.live.com/view.aspx?resid=273538595B398734!133&ithint=onenote%2c&app=OneNote&authkey=!ABbDfXT81mMyGkQ)

## Trello Backlog
* New: 02-19-2016
* [Trello Backlog] (https://trello.com/invite/knightwriterscs383/f97390a46c8bb03a20cc124a75af9ae5)

## Homework 4 Document
* new
* [Homework 4 Master Doc] (https://www.overleaf.com/4388669wxqmrp)

## Class Diagrams
* put diagrams and descriptions here
* [Class Diagrams](https://www.overleaf.com/4361110npqmqd)

## Functional/Non-Functional Requirements
* was called "Homework 3"
* needs updating, mostly there
* [Our Requirements Doc](https://www.overleaf.com/4286506kvbrwb#/12707218/)
* [Aggregate Requirements Doc](https://www.overleaf.com/4306618jzdznq)

## Use Cases
## Homework 1's links:
* Team 1: [pdf](http://www2.cs.uidaho.edu/~jeffery/courses/383/hw1-team1.pdf)

* Team 2: [pdf](http://www2.cs.uidaho.edu/~jeffery/courses/383/hw1-team2.pdf)

* Team 3: [pdf](http://www2.cs.uidaho.edu/~jeffery/courses/383/hw1-team3.pdf)

* Team 4: [pdf](http://www2.cs.uidaho.edu/~jeffery/courses/383/hw1-team4.pdf)

## Homework 2's links:
Team A: [Overleaf] (https://www.overleaf.com/4236744jrrqpg#/12532352/)
Team C: [Overleaf] (https://www.overleaf.com/4236690nydzfb#/12532109/)

