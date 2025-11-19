# MiniGit 
MiniGit is a simplified version-control system inspired by Git, built entirely in Java.
It supports essential Git-like operations such as committing, branching, switching, staging, checking out previous snapshots, and comparing file differences.


# Features
Features
1) Working Directory Operations
  write <file> – Create or overwrite a file (multi-line input)
  read <file> – Display file content
  edit <file> – Replace entire file content
  append <file> – Add new content at the end of the file
  delete <file> – Remove a file from working directory

2) Staging & Committing
  add <file> – Stage file for commit
  commit <message> – Create a new snapshot
  ✔ Stores full snapshot (Git-like immutability)
  ✔ Includes timestamp for each commit
  ✔ Each commit holds a parent pointer (linked commit chain)

3) Repository Inspection
  status – Shows staged, modified, and untracked files
  log – View full commit history
  show <commitID> – Display snapshot content of a commit
  show-working – Display current working directory state

4) Checkout & Version Navigation
  checkout HEAD – Restore the latest commit snapshot
  checkout <ID> – Jump to a specific commit

5) Branching
  branch <name> – Create a new branch at current HEAD
  list-branches – View all branches
  switch <name> – Switch between branches
  ✔ Prevents switching if there are unstaged or staged changes
  ✔ Updates HEAD pointer automatically

6) Difference Tools
  diff <c1> <c2> – Show added / removed / modified files
  diff-content <c1> <c2> – Show full content differences

# Architecture Overview

# Repository (Core Controller)
Manages the working directory, staging area, branches, and HEAD.
Acts as the central engine that processes user commands.
Stores:
  HashMap<String, String> → Working Directory
  HashSet<String> → Staging Area
  HashMap<String, Commit> → Branches
  String → Current Branch
  Commit → HEAD Commit

# Commit Object (Unit of Version Control)
Each commit contains:
    Commit ID
    Message
    Timestamp
    Snapshot (full file copy using HashMap)
    Parent Pointer to previous commit
This forms a simple Git-like immutable commit object.

# Branch Structure
Each branch stores a pointer to its HEAD commit.
Implemented using HashMap<String, Commit>.
Switching branches updates:
Current branch name
HEAD pointer

# Commit History Model 
Every commit remembers the commit that came before it.
This creates a chain of commits, one after another (like links in a chain).
Because each commit points to only one previous commit, the history is straight and simple — not branching like real Git merges.
This gives you a clean, easy-to-understand version history similar to Git, but without the complexity of merges.
Working directory snapshot

# Key Data Structures
Component	             Type	                                 Purpose
Working Directory	  HashMap<String,String>       	Current user-edited files
Staging Area	       HashSet<String>	            Tracks files added for commit
Branches	         HashMap<String,Commit>         Maps branch name → HEAD commit
Commit	             Custom class	                 Stores snapshot & metadata
