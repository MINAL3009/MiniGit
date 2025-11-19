import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Repository {
    HashMap<String, String> workingDirectory;
    HashSet<String> stagingArea;
    HashMap<String, Commit> branches;
    String currentBranch;
    Commit head;

    public Repository() {
        // init command logic
        workingDirectory = new HashMap<>();
        stagingArea = new HashSet<>();
        branches = new HashMap<>();
        currentBranch = "main";
        head = null;
        branches.put(currentBranch, head);
        System.out.println("Initialized empty MiniGit repository on branch 'main'.");
    }

    public void add(String filename) {
        if (!workingDirectory.containsKey((filename))) {
            System.out.println("Error: File '" + filename + "' does not exist in working directory.");
            return;
        }
        stagingArea.add(filename);
        System.out.println("Staged: " + filename);
    }

    public void commit(String message) {
        if (stagingArea.isEmpty()) {
            System.out.println("Nothing to commit. Staging area is empty.");
            return;
        }

        // Step 1: start snapshot as parent snapshot (copy)
        HashMap<String, String> snapshot = new HashMap<>();
        if (head != null) {
            snapshot.putAll(head.snapshot);
        }

        // Step 2: override/add ONLY staged files
        for (String file : stagingArea) {
            snapshot.put(file, workingDirectory.get(file));
        }

        // Step 3: create commit
        String commitID = UUID.randomUUID().toString().substring(0, 6);
        Commit newCommit = new Commit(commitID, message, snapshot, head);

        head = newCommit;
        branches.put(currentBranch, head);

        stagingArea.clear();

        System.out.println("[" + currentBranch + "] Commit " + commitID);
        System.out.println("Message: " + message);
    }

    public void log() {
        Commit temp = head;
        if (temp == null) {
            System.out.println("No commits yet.");
            return;
        }

        while (temp != null) {
            System.out.println("Commit " + temp.commitID);
            System.out.println("Message: " + temp.message);
            System.out.println("Time: " + temp.timestamp);
            System.out.println("------------------------");
            temp = temp.parent; // Move to previous commit
        }
    }

    // checking the status of the files
    public void status() {
        System.out.println("=== Staged Files ===");
        for (String file : stagingArea) {
            System.out.println(file);
        }

        System.out.println("\n=== Modified Files ===");
        if (head != null) {
            for (String file : workingDirectory.keySet()) {
                String workingContent = workingDirectory.get(file);
                String committedContent = head.snapshot.get(file);
                if (committedContent != null && !workingContent.equals(committedContent)
                        && !stagingArea.contains(file)) {
                    System.out.println(file);
                }
            }
        }
        System.out.println("\n===Untracked Files ===");
        if (head != null) {
            for (String file : workingDirectory.keySet()) {
                if (!head.snapshot.containsKey(file) && !stagingArea.contains(file)) {
                    System.out.println(file);
                }
            }
        } else {
            // If no commit exists, everything is untracked
            for (String file : workingDirectory.keySet()) {
                if (!stagingArea.contains(file)) {
                    System.out.println(file);
                }
            }
        }
    }
    // checkout to previous commits

    public void checkout(String commitID) {
        // case 1 checkoutt head
        if (commitID.equals("HEAD")) {
            if (head == null) {
                System.out.println("No commits yet. Nothing to checkout.");
                return;
            }
            // restore the latest commit snapshot
            workingDirectory = new HashMap<>(head.snapshot);
            stagingArea.clear();
            System.out.println("Checked out to HEAD (latest commit).");
            return;
        }
        Commit temp = head;
        while (temp != null) {
            if (temp.commitID.equals(commitID)) {

                // Restore snapshot into working directory
                workingDirectory = new HashMap<>(temp.snapshot);
                stagingArea.clear();
                System.out.println("Checked out to commit " + commitID);
                return;
            }
            temp = temp.parent;
        }
        System.out.println("Error: Commit ID '" + commitID + "' not found.");
    }

    // method for the testing purpose to just test the system is working properly
    // after commit
    public void showWorkingDirectory() {
        System.out.println("=== Working Directory ===");
        if (workingDirectory.isEmpty()) {
            System.out.println("(empty)");
            return;

        }
        for (String file : workingDirectory.keySet()) {
            System.out.println("\nFile: " + file);
            System.out.println(workingDirectory.get(file));
            System.out.println("--------------------------------");
        }
    }

    /*
     * ✔ Step 2 — In Main.java, inside your command loop, add:
     * else if (command.equals("show-working")) {
     * repo.showWorkingDirectory();
     * }
     * 
     * So now you can type:
     * 
     * show-working
     * 
     * in the terminal and see the working directory.
     */

    public void branch(String branchName) {
        // Rule 1: branch name already exists
        if (branches.containsKey(branchName)) {
            System.out.println("Error: Branch '" + branchName + "' already exists.");
            return;
        }
        // Rule 2: cannot create branch without commits
        if (head == null) {
            System.out.println("Error: No commits yet. Cannot create branch.");
            return;
        }

        // Create new branch pointing to current HEAD
        branches.put(branchName, head);
        System.out.println("Branch '" + branchName + "' created at commit " + head.commitID);
    }
    /*
     * 5. Add command in Main.java
     * 
     * Inside your command loop:
     * 
     * else if (command.equals("branch")) {
     * String branchName = parts[1]; // whatever you do for parsing
     * repo.branch(branchName);
     * }
     */

    public void switchBranch(String branchName) {
    // Rule 1: Branch must exist
    if (!branches.containsKey(branchName)) {
        System.out.println("Error: Branch '" + branchName + "' does not exist.");
        return;
    }

    // Rule 2: Cannot switch with staged changes
    if (!stagingArea.isEmpty()) {
        System.out.println("Error: You have staged changes. Commit or clear them before switching.");
        return;
    }

    // Rule 3: Cannot switch with modified but unstaged files
    if (head != null) {
        for (String file : workingDirectory.keySet()) {
            String work = workingDirectory.get(file);
            String committed = head.snapshot.get(file);

            if (committed != null && !work.equals(committed)) {
                System.out.println("Error: You have unstaged modifications. Commit or discard changes before switching.");
                return;
            }
        }
    }

    // perform the actual switch
    currentBranch = branchName;
    head = branches.get(branchName);

    // restore working directory from branch head
    if (head != null) {
        workingDirectory = new HashMap<>(head.snapshot);
    } else {
        workingDirectory.clear();
    }

    System.out.println("Switched to branch '" + branchName + "'.");
}

    /*
     * 5. Add command in Main.java
     * 
     * Inside your command parser:
     * 
     * else if (command.equals("switch")) {
     * String branchName = parts[1];
     * repo.switchBranch(branchName);
     * }
     * 
     */

    // helper method to find the commit from its commit ID

    private Commit findCommit(String commitID) {
        Commit temp = head;
        while (temp != null) {
            if (temp.commitID.equals(commitID)) {
                return temp;
            }
            temp = temp.parent;
        }
        return null;
    }

    // implement the diff method

    public void diff(String commitID1, String commitID2) {
        Commit c1 = findCommit(commitID1);
        Commit c2 = findCommit(commitID2);
        if (c1 == null || c2 == null) {
            System.out.println("Error: One or both commit IDs not found.");
            return;
        }
        HashMap<String, String> snap1 = c1.snapshot;
        HashMap<String, String> snap2 = c2.snapshot;

        System.out.println("\n=== Added Files ===");
        for (String file : snap2.keySet()) {
            if (!snap1.containsKey(file)) {
                System.out.println(file);
            }
        }

        System.out.println("\n=== Removed Files ===");
        for (String file : snap1.keySet()) {
            if (!snap2.containsKey(file)) {
                System.out.println(file);
            }
        }

        System.out.println("\n=== Modified Files ===");
        for (String file : snap1.keySet()) {
            if (snap2.containsKey(file)) {
                String c1Content = snap1.get(file);
                String c2Content = snap2.get(file);
                if (!c1Content.equals(c2Content)) {
                    System.out.println(file);
                }
            }
        }

    }
    // additional feature to get the exact content in case of teh modified files

    public void diffContent(String commitId1, String commitId2) {

        Commit c1 = findCommit(commitId1);
        Commit c2 = findCommit(commitId2);

        if (c1 == null || c2 == null) {
            System.out.println("Error: One or both commit IDs not found.");
            return;
        }

        HashMap<String, String> snap1 = c1.snapshot;
        HashMap<String, String> snap2 = c2.snapshot;

        System.out.println("\n=== Content Differences ===");

        boolean found = false;

        for (String file : snap1.keySet()) {
            if (snap2.containsKey(file)) {

                String oldContent = snap1.get(file);
                String newContent = snap2.get(file);

                if (!oldContent.equals(newContent)) {
                    found = true;

                    System.out.println("\nFile: " + file);
                    System.out.println("--- Old Content ---");
                    System.out.println(oldContent);

                    System.out.println("--- New Content ---");
                    System.out.println(newContent);
                }
            }
        }

        if (!found) {
            System.out.println("No content differences found.");
        }
    }

    // list branches
    public void listBranches() {
        System.out.println("=== Branches ===");

        for (String branch : branches.keySet()) {
            if (branch.equals(currentBranch)) {
                System.out.println(branch + " *"); // mark current branch
            } else {
                System.out.println(branch);
            }
        }
    }

    // show commit by its commitID
    public void showCommit(String commitID) {

        Commit commit = findCommit(commitID);

        if (commit == null) {
            System.out.println("Error: Commit '" + commitID + "' not found.");
            return;
        }

        System.out.println("=== Snapshot of commit " + commitID + " ===");
        System.out.println("Date: " + commit.timestamp);

        if (commit.snapshot.isEmpty()) {
            System.out.println("(Empty snapshot)");
            return;
        }

        for (String file : commit.snapshot.keySet()) {
            System.out.println("\nFile: " + file);
            System.out.println(commit.snapshot.get(file));
            System.out.println("------------------------------");
        }
    }

    // method for the read command :Shows content of a file from workingDirectory.
    public void readFile(String filename) {
        if (!workingDirectory.containsKey(filename)) {
            System.out.println("Error: File '" + filename + "' does not exist.");
            return;
        }

        System.out.println("=== " + filename + " ===");
        System.out.println(workingDirectory.get(filename));
    }

    // method for the delete command :Removes a file from workingDirectory.
    public void deleteFile(String filename) {
        if (!workingDirectory.containsKey(filename)) {
            System.out.println("Error: File '" + filename + "' does not exist.");
            return;
        }

        workingDirectory.remove(filename);
        System.out.println("Deleted file: " + filename);
    }

    // method to edit the file : overwrite the previous one
   public void editFile(String filename, java.util.Scanner sc) {
    if (!workingDirectory.containsKey(filename)) {
        System.out.println("Error: File '" + filename + "' does not exist.");
        return;
    }

    System.out.println("Existing content:");
    System.out.println(workingDirectory.get(filename));

    System.out.println("\nEnter new content (type END on a new line to finish):");
    StringBuilder sb = new StringBuilder();

    while (true) {
        String line = sc.nextLine();
        if (line.trim().equalsIgnoreCase("END")) break;
        sb.append(line).append("\n");
    }

    workingDirectory.put(filename, sb.toString().trim());
    System.out.println("Updated content in " + filename);
}


    // method to edit the file just append to the existed one
   public void appendFile(String filename, java.util.Scanner sc) {

    if (!workingDirectory.containsKey(filename)) {
        System.out.println("Error: File '" + filename + "' does not exist.");
        return;
    }

    System.out.println("Existing content:");
    System.out.println(workingDirectory.get(filename));

    System.out.println("\nEnter content to append (type END on a new line to finish):");

    StringBuilder sb = new StringBuilder();

    while (true) {
        String line = sc.nextLine();

        // FIX: allow END, end, End, END  (with spaces)
        if (line.trim().equalsIgnoreCase("END")) break;

        sb.append(line).append("\n");
    }

    // append new text to the old content
    String oldContent = workingDirectory.get(filename);
    String newContent = oldContent + "\n" + sb.toString().trim();

    workingDirectory.put(filename, newContent);
    System.out.println("Appended content to " + filename);
}


}





