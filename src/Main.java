import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String filename;
        VariableList varList = new VariableList();
        CodeBlock codeBlock = new CodeBlock(0, "");
        System.out.println("Input file name:");
        filename = new Scanner(System.in).nextLine() + ".txt";
        Packet packet = new Packet(varList, new CodeList());
        try {
            File codeToRead = new File(filename);
            Scanner fileReader = new Scanner(codeToRead);
            CodeList codeList = new CodeList();
            while (fileReader.hasNext()) {
                codeList.addCode(fileReader.nextLine());
            }
            packet = new Packet(varList, codeList);
            packet = codeBlock.loop(packet);
        } catch (FileNotFoundException e) {
            System.out.println("Error (4): File not found: " + filename);
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Error (0): Unknown error. Please contact your system administrator to resolve this issue.");
            System.out.println(e.getMessage());
            System.exit(0);
        }
        System.out.println("Code execution complete!");
        System.out.println("Final variable states are as follows:");
        packet.printAllVars();
    }
}