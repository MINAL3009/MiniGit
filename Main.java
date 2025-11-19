import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Repository repo = new Repository();

        System.out.println("\n=== MiniGit Console ===");
        System.out.println("Type 'help' to see all commands.\n");

        while (true) {
            System.out.print("> ");
            String input = sc.nextLine().trim();

            if (input.equals("")) continue;

            String[] parts = input.split(" ", 2);
            String command = parts[0];

            // ================= EXIT ====================
            if (command.equals("exit")) {
                System.out.println("Exiting MiniGit...");
                break;
            }

            // ================= HELP ====================
            else if (command.equals("help")) {
                printHelp();
            }

            // ================= WRITE FILE (MULTI-LINE) ====================
            else if (command.equals("write")) {
                if (parts.length < 2) {
                    System.out.println("Usage: write <filename>");
                    continue;
                }

                String filename = parts[1];
                System.out.println("Enter content for " + filename + " (type END on a new line to finish):");

                StringBuilder contentBuilder = new StringBuilder();

                while (true) {
                    String line = sc.nextLine();
                    if (line.trim().equalsIgnoreCase("END")) break;

                    contentBuilder.append(line).append("\n");
                }

                String content = contentBuilder.toString().trim();

                repo.workingDirectory.put(filename, content);
                System.out.println("Written (multi-line) to " + filename);
            }

            // ================= READ FILE ====================
            else if (command.equals("read")) {
                if (parts.length < 2) {
                    System.out.println("Usage: read <filename>");
                    continue;
                }
                repo.readFile(parts[1]);
            }

            // ================= EDIT FILE (OVERWRITE) ====================
            else if (command.equals("edit")) {
                if (parts.length < 2) {
                    System.out.println("Usage: edit <filename>");
                    continue;
                }
                repo.editFile(parts[1], sc);
            }

            // ================= APPEND FILE ====================
            else if (command.equals("append")) {
                if (parts.length < 2) {
                    System.out.println("Usage: append <filename>");
                    continue;
                }
                repo.appendFile(parts[1], sc);
            }

            // ================= DELETE FILE ====================
            else if (command.equals("delete")) {
                if (parts.length < 2) {
                    System.out.println("Usage: delete <filename>");
                    continue;
                }
                repo.deleteFile(parts[1]);
            }

            // ================= ADD FILE TO STAGING ====================
            else if (command.equals("add")) {
                if (parts.length < 2) {
                    System.out.println("Usage: add <filename>");
                    continue;
                }
                repo.add(parts[1]);
            }

            // ================= COMMIT ====================
            else if (command.equals("commit")) {
                if (parts.length < 2) {
                    System.out.println("Usage: commit <message>");
                    continue;
                }
                repo.commit(parts[1]);
            }

            // ================= LOG ====================
            else if (command.equals("log")) {
                repo.log();
            }

            // ================= STATUS ====================
            else if (command.equals("status")) {
                repo.status();
            }

            // ================= CHECKOUT ====================
            else if (command.equals("checkout")) {
                if (parts.length < 2) {
                    System.out.println("Usage: checkout <commitID | HEAD>");
                    continue;
                }
                repo.checkout(parts[1]);
            }

            // ================= SHOW WORKING DIRECTORY ====================
            else if (command.equals("show-working")) {
                repo.showWorkingDirectory();
            }

            // ================= BRANCH ====================
            else if (command.equals("branch")) {
                if (parts.length < 2) {
                    System.out.println("Usage: branch <branchName>");
                    continue;
                }
                repo.branch(parts[1]);
            }

            // ================= SWITCH BRANCH ====================
            else if (command.equals("switch")) {
                if (parts.length < 2) {
                    System.out.println("Usage: switch <branchName>");
                    continue;
                }
                repo.switchBranch(parts[1]);
            }

            // ================= LIST BRANCHES ====================
            else if (command.equals("list-branches")) {
                repo.listBranches();
            }

            // ================= DIFF ====================
            else if (command.equals("diff")) {
                String[] argsSplit = input.split(" ");
                if (argsSplit.length < 3) {
                    System.out.println("Usage: diff <commit1> <commit2>");
                    continue;
                }
                repo.diff(argsSplit[1], argsSplit[2]);
            }

            // ================= DIFF CONTENT ====================
            else if (command.equals("diff-content")) {
                String[] argsSplit = input.split(" ");
                if (argsSplit.length < 3) {
                    System.out.println("Usage: diff-content <commit1> <commit2>");
                    continue;
                }
                repo.diffContent(argsSplit[1], argsSplit[2]);
            }

            // ================= SHOW COMMIT SNAPSHOT ====================
            else if (command.equals("show")) {
                if (parts.length < 2) {
                    System.out.println("Usage: show <commitID>");
                    continue;
                }
                repo.showCommit(parts[1]);
            }

            // ================= UNKNOWN COMMAND ====================
            else {
                System.out.println("Unknown command. Type 'help' to see list of commands.");
            }
        }

        sc.close();
    }

    // PRINT HELP MENU
    static void printHelp() {
        System.out.println("\n=== Available Commands ===");

        System.out.println("write <filename>        - Create or overwrite file (multi-line input)");
        System.out.println("read <filename>         - Display file content");
        System.out.println("edit <filename>         - Overwrite entire file content");
        System.out.println("append <filename>       - Add content to end of file");
        System.out.println("delete <filename>       - Remove file from working directory");

        System.out.println("add <filename>          - Stage file for commit");
        System.out.println("commit <message>        - Create new commit");
        System.out.println("log                     - Show commit history");
        System.out.println("status                  - Show staged, modified, untracked files");

        System.out.println("checkout <ID|HEAD>      - Restore old commit or latest commit");

        System.out.println("branch <name>           - Create new branch");
        System.out.println("switch <name>           - Switch to another branch");
        System.out.println("list-branches           - Display all branches");

        System.out.println("diff <c1> <c2>          - Show added / removed / modified files");
        System.out.println("diff-content <c1> <c2>  - Show actual line differences");

        System.out.println("show <commitID>         - Show full snapshot of a commit");
        System.out.println("show-working            - View working directory files");

        System.out.println("help                    - Show all commands");
        System.out.println("exit                    - Quit MiniGit");

        System.out.println("===========================\n");
    }
}
