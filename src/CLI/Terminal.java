package CLI;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Stream;

public class Terminal {
    public static String workDir;
    public Terminal(){
        workDir = System.getProperty("user.dir") + "\\src\\CLI\\";
    }
    public String getWorkDirectory(){
        return workDir;
    }

    public void setWorkDirectory(String path){
        workDir = path;
    }

    public static boolean PathExist(String path){
        File file = new File(path);
        return file.exists();
    }
    public String Absolute(String path){
        File file = new File(path);
        if(file.isAbsolute()){
            return path;
        }
        return workDir + path;
    }
    public static boolean IsFile(String path){
        File file = new File(path);
        return file.isFile();
    }
    public static boolean IsDirectory(String path){
        File file = new File(path);
        return file.isDirectory();
    }
    public String readFile(String path) {
        String result = "";
        if (!PathExist(path)) {
            result = "Error:This Path Not Exist.";
        } else if (!IsFile(path)) {
            result = "Error:This Path Related to Directory Not File.";
        } else {
            try {
                File file = new File(path);
                Scanner readScanner = new Scanner(file);
                while (readScanner.hasNextLine()) {
                    result += readScanner.nextLine() + "\n";
                }
                readScanner.close();
            } catch (FileNotFoundException e) {
                result = "Error:Cant Open File.";
            }
        }
        return result;
    }
    public String writeFile(String path, String data) {
        String result = "";
        if (!PathExist(path)) {
            result = "Error:This Path Not Exist.";
        } else if (!IsFile(path)) {
            result = "Error:This Path Related to Directory Not File.";
        } else {
            try {
                FileWriter obWriter = new FileWriter(path);
                obWriter.write(data);
                obWriter.flush();
                obWriter.close();
                result = "Successfully wrote to the file.";
            } catch (IOException e) {
                result = "Error:Cant Open File For Write.";
            }
        }
        return result;
    }
    public String createFile(String path){
        File myObj = new File(path);
        String result = myObj.getAbsolutePath();
        try {
            if (myObj.createNewFile()) {
                 result = myObj.getAbsolutePath();
            } else {
                result = "File already exists.";
            }
        } catch (IOException e) {
            result = "An error occurred.";
        }
        return result;
    }
    public String ls(String path){
        String[] ListOfFiles = {};
        String result = "";
        if (!PathExist(path)) {
            result = "Error:This Path Not Exist.";
        } else if (!IsDirectory(path)) {
            result = "Error:This Path Related to File Not Directory.";
        } else {
            File directoryPath = new File(path);
            ListOfFiles = directoryPath.list();
            for(int counter = 0; counter < ListOfFiles.length;  counter++) {
                if(IsDirectory(ListOfFiles[counter])){
                    result += "*" + ListOfFiles[counter] + "\t\t\t";
                }else{
                    result += "-" + ListOfFiles[counter] + "\t\t\t";
                }
                if(counter%2 != 0){
                    result += "\n";
                }
            }
        }
        return result;
    }

    public String ls(){
        return ls(workDir);
    }

    public String cd(String path){
        if(path.equals("..")){
            Path mypath = Paths.get(workDir);
            Path parentPath = mypath.getParent();
            workDir = parentPath.toString();
        }
        else if(PathExist(path)){
            workDir = path;
        }else{
            System.out.println("path doesn't exist.");
        }
        return workDir;
    }

    public String cp(String sorcePath, String destinationPath){
        String newFilePath = destinationPath;
        if (!PathExist(sorcePath)) {
            System.out.println("Error:This Path Not Exist.");
        } else if (!IsFile(sorcePath)) {
            System.out.println("Error:This Path Related to Directory Not File.");
        } else {
            File fileName = new File(sorcePath);
            String content = readFile(sorcePath);
            if(PathExist(destinationPath) && IsFile(destinationPath)){
                writeFile(newFilePath, content);
            }else{
                newFilePath = createFile(destinationPath);
                System.out.println(newFilePath);
                writeFile(newFilePath, content);
            }
        }
        return newFilePath;
    }

    public String cat(String path){
        return readFile(path);
    }

    public String more(String path){
        String Continue = "";
        Scanner input = new Scanner(System.in);
        String content = readFile(path);
        String[] lines = content.split("\\r?\\n");
        int counter = 0;
        content = "";
        while(lines.length > counter){
            for (int i = 0; i < 5 && counter < lines.length  ; i++,counter++){
                System.out.println(lines[counter]);
                content += lines[counter] + "\n";
            }
            Continue = input.next();
            if(Continue.equals("q")){
                break;
            }
        }
        return content;
    }
    public String date(){ //function to print the current date and time
        Date date = new Date();
        return (date.toString());
    }

    public String mkdir(String path){

        File newFolder = new File(path);

        if(newFolder.getParentFile().exists() && newFolder.mkdir())
        {
            return path;
        }
        else if(newFolder.exists())
        {
            return ("File already exists in this directory");
        }
        else {
            return ("An error occurred while creating file.");
        }


    }

    public String rmdir(String path){
        File newFolder = new File(path);
        String folders = ls(path);
        if(folders.equals(""))
        {
            newFolder.delete();
            return "Directory deleted";
        }
        else {
            return "Directory is not empty, can not delete it";
        }
        /*mkdir(path);
        String[] folders = ls(path);
        if(folders.length() == 0)
        {
            rm(path);
            System.out.println("Directory deleted");
        }
        else {
            System.out.println("Directory is not empty, can not delete it");
        }*/
    }
    public String mv(String sourcePath, String destinationPath){

        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);
        try {
            destinationFile.createNewFile();
        } catch (Exception e) {
            System.out.println("An error occurred.");
        }

        if(destinationFile.exists() && destinationFile.isFile()){
            String fileContent = readFile(sourceFile.getPath());
            writeFile(destinationFile.getPath(),fileContent);
            rm(sourceFile.getPath());
        }
        else if(destinationFile.exists() && destinationFile.isDirectory()){
            String fileContent = readFile(sourceFile.getPath());
            File newFile = new File(destinationFile.getPath()+"\\"+sourceFile.getName());
            try {
                newFile.createNewFile();
                writeFile(newFile.getPath(),fileContent);
            } catch (Exception e) {
                System.out.println("An error occurred.");
            }
            rm(sourceFile.getPath());

        }
        return destinationPath;
    }
    public String rm(String path){
        File newFile = new File(path);

        try {
            if(newFile.exists()){
                newFile.delete();
            }
        } catch (Exception e) {
            System.out.println("File not found");
        }
        return path;
    }
    public String pwd(){
        return getWorkDirectory();
        //print current path
    }
    public String args(String cmd){
        String command = cmd.toLowerCase();
        String argsMessage = "";
        switch (command) {
            case "cd":
                argsMessage += ("arg1: new directory path");
                break;
            case "ls":
            case "rm":
            case "rmdir":
            case "mkdir":
            case "more":
            case "cat":
                argsMessage += ("arg1: directory path");
                break;
            case "cp":
            case "mv":
                argsMessage += ("arg1: source path  -  arg2: destination path");
                break;
            case "args":
                argsMessage += ("arg1: command name");
                break;
            case "date":
            case "help":
            case "pwd":
            case "clear":
                argsMessage += ("no arguments");
                break;
            default:
                argsMessage += ("Please enter a correct command and to know the commands type HELP");
                break;
        }
        return argsMessage;

    }
    public String help()
    {
        String helpMessage = "";
        helpMessage+=("Commands \t\t\t\t Arguments                               \t\t\t\t Usage\n");
        helpMessage+=("cd       \t\t\t\t 1- new directory path                   \t\t\t\t move from one directory to another\n");
        helpMessage+=("ls       \t\t\t\t 1- directory path                       \t\t\t\t list files and folders in this directory\n");
        helpMessage+=("cp       \t\t\t\t 1- source path \t 2- destination path \t\t\t\t copy from one file/directory to another\n");
        helpMessage+=("cat      \t\t\t\t 1- directory path                       \t\t\t\t print content of a file\n");
        helpMessage+=("more     \t\t\t\t 1- directory path                       \t\t\t\t print specific part of the content of a file\n");
        helpMessage+=("mkdir    \t\t\t\t 1- directory path                       \t\t\t\t create a folder\n");
        helpMessage+=("rmdir    \t\t\t\t 1- directory path                       \t\t\t\t remove the folder if it is empty\n");
        helpMessage+=("mv       \t\t\t\t 1- source path \t 2- destination path \t\t\t\t move from one file/directory to another\n");
        helpMessage+=("rm       \t\t\t\t 1- directory path                       \t\t\t\t remove the folder/file\n");
        helpMessage+=("args     \t\t\t\t 1- command name                         \t\t\t\t list the parameters for specific command.\n");
        helpMessage+=("date     \t\t\t\t no arguments                            \t\t\t\t print the current date and time\n");
        helpMessage+=("help     \t\t\t\t no arguments                            \t\t\t\t list all commands and their arguments\n");
        helpMessage+=("pwd      \t\t\t\t no arguments                            \t\t\t\t print the current directory path\n");
        helpMessage+=("clear    \t\t\t\t no arguments                            \t\t\t\t clear the command line screen\n");
        return helpMessage;

    }

    public String clear(){
        //System.out.flush();
        String clearMessage = "";
        for (int i = 0; i<40;i++){
            clearMessage += "\n";
        }
        return clearMessage;
    }

}
