import java.util.*;

// =============================================================
//                     MAIN CLASS
// =============================================================
public class Main {

    // --------------------- Command Interface ----------------------
    interface Command {
        void execute();
        void undo();
    }

    // --------------------- Memento Class --------------------------
    static class EditorMemento {
        private final String text;

        public EditorMemento(String text) {
            this.text = text;
        }

        public String getSavedText() {
            return text;
        }
    }

    // -------------------- Text Editor Class -----------------------
    static class TextEditor {
        private String text = "";

        public void insertText(String newText) {
            text += newText + " ";
        }

        public void deleteWord(String word) {
            String[] words = text.trim().split(" ");
            List<String> list = new ArrayList<>(Arrays.asList(words));

            // remove last occurrence of the word
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i).equals(word)) {
                    list.remove(i);
                    break;
                }
            }

            text = String.join(" ", list) + " ";
        }

        public String getText() {
            return text;
        }

        public void setText(String newText) {
            text = newText;
        }

        public EditorMemento save() {
            return new EditorMemento(text);
        }

        public void restore(EditorMemento memento) {
            text = memento.getSavedText();
        }
    }

    // ---------------------- History Class -------------------------
    static class History {
        private Stack<EditorMemento> undoStack = new Stack<>();
        private Stack<EditorMemento> redoStack = new Stack<>();

        public void saveState(EditorMemento memento) {
            undoStack.push(memento);
            redoStack.clear();
        }

        public EditorMemento undo(EditorMemento currentState) {
            if (!undoStack.isEmpty()) {
                redoStack.push(currentState);
                return undoStack.pop();
            }
            return null;
        }

        public EditorMemento redo(EditorMemento currentState) {
            if (!redoStack.isEmpty()) {
                undoStack.push(currentState);
                return redoStack.pop();
            }
            return null;
        }
    }

    // ---------------------- Insert Command -------------------------
    static class InsertCommand implements Command {
        private final TextEditor editor;
        private final History history;
        private final String text;

        public InsertCommand(TextEditor editor, History history, String text) {
            this.editor = editor;
            this.history = history;
            this.text = text;
        }

        @Override
        public void execute() {
            history.saveState(editor.save());
            editor.insertText(text);
        }

        @Override
        public void undo() {
            EditorMemento prev = history.undo(editor.save());
            if (prev != null) editor.restore(prev);
        }
    }

    // ---------------------- Delete Command -------------------------
    static class DeleteWordCommand implements Command {
        private final TextEditor editor;
        private final History history;
        private final String word;

        public DeleteWordCommand(TextEditor editor, History history, String word) {
            this.editor = editor;
            this.history = history;
            this.word = word;
        }

        @Override
        public void execute() {
            history.saveState(editor.save());
            editor.deleteWord(word);
        }

        @Override
        public void undo() {
            EditorMemento prev = history.undo(editor.save());
            if (prev != null) editor.restore(prev);
        }
    }

    // =============================================================
    //                     CONSOLE UI
    // =============================================================
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        TextEditor editor = new TextEditor();
        History history = new History();

        while (true) {
            System.out.println("\nCurrent Text: " + editor.getText());
            System.out.println("1. Insert Text");
            System.out.println("2. Delete Word");
            System.out.println("3. Undo");
            System.out.println("4. Redo");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter text: ");
                    String text = sc.nextLine();
                    new InsertCommand(editor, history, text).execute();
                    break;

                case 2:
                    System.out.print("Enter word to delete: ");
                    String word = sc.nextLine();
                    new DeleteWordCommand(editor, history, word).execute();
                    break;

                case 3:
                    EditorMemento prev = history.undo(editor.save());
                    if (prev != null) editor.restore(prev);
                    else System.out.println("Nothing to undo!");
                    break;

                case 4:
                    EditorMemento next = history.redo(editor.save());
                    if (next != null) editor.restore(next);
                    else System.out.println("Nothing to redo!");
                    break;

                case 5:
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
