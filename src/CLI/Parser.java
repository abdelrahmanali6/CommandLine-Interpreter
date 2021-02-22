package CLI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Parser {
    static Scanner input = new Scanner(System.in);
    static boolean OperatorExist = false;
    static ArrayList<String> operators = new ArrayList<String>();
    String operator;
    private String[][] cmds = new String[19][2];
    Terminal Obj = new Terminal();

    public Parser()
    {
        cmds[0][0]="cd"; cmds[0][1]="1";
        cmds[1][0]="ls"; cmds[1][1]="0";
        cmds[2][0]="cp"; cmds[2][1]="2";
        cmds[3][0]="cat"; cmds[3][1]="1";
        cmds[4][0]="more"; cmds[4][1]="1";
        cmds[5][0]="mkdir"; cmds[5][1]="1";
        cmds[6][0]="rmdir"; cmds[6][1]="1";
        cmds[7][0]="mv"; cmds[7][1]="2";
        cmds[8][0]="rm"; cmds[8][1]="1";
        cmds[9][0]="args"; cmds[9][1]="1";
        cmds[10][0]="date"; cmds[10][1]="0";
        cmds[11][0]="help"; cmds[11][1]="0";
        cmds[12][0]="pwd"; cmds[12][1]="0";
        cmds[13][0]="clear"; cmds[13][1]="0";
        cmds[14][0]="ls"; cmds[14][1]="0";
        cmds[15][0]=">"; cmds[15][1]="1";
        cmds[16][0]=">>"; cmds[16][1]="1";
    }

    public boolean GetOperatorExists()
    {
        return OperatorExist;
    }
    public ArrayList<String> GetOperators(){

        return operators;
    }
    public String GetOperator(){return operator;}

    public ArrayList<ArrayList<String>> SplitInput()
    {
        String UserInput = input.nextLine();
        List<String> list = Arrays.asList(UserInput.split("\\s+"));
        list.removeIf(Objects::isNull);
        return SeperateCommands(list);
    }

    public ArrayList<ArrayList<String>> SeperateCommands(List<String> cmd)
    {
        int lastLocation=0;
        ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
        operators = new ArrayList<String>();
        OperatorExist = false;
        operator = "";
        commands.add(new ArrayList<String>());
        for(int i=0; i<cmd.size(); i++)
        {
            if(cmd.get(i).equals("|") || cmd.get(i).equals(">") || cmd.get(i).equals(">>"))
            {
                OperatorExist = true;
                if(operators.size() == 0)
                {
                    operators.add(cmd.get(i));
                }
                else if(operators.size() > 0 && !cmd.get(i).equals(operators.get(0)))
                {
                    commands = new ArrayList<ArrayList<String>>();
                    System.out.println("Incorrect Commands");
                    return commands;
                }
                operators.add(cmd.get(i));
                operator = cmd.get(i);
                commands.add(new ArrayList<String>());
            }
        }



        for(int i=0; i<commands.size(); i++)
        {
            for(int j= lastLocation; j<cmd.size(); j++)
            {
                if(!OperatorExist)
                {
                    commands.set(i,new ArrayList<>(cmd.subList(lastLocation,cmd.size())));
                    lastLocation = cmd.size();
                    break;
                }
                else if((cmd.get(j).equals("|") || cmd.get(j).equals(">>") || cmd.get(j).equals(">") || j == cmd.size()-1) && j != lastLocation )
                {

                    if(j == cmd.size()-1)
                    {
                        commands.set(i, new ArrayList<>(cmd.subList(lastLocation, j+1)));
                    }
                    else {

                        commands.set(i, new ArrayList<>(cmd.subList(lastLocation, j)));
                    }

                    if(cmd.get(j).equals(">>") || cmd.get(j).equals(">")) {
                        lastLocation = j;
                    }
                    else if(cmd.get(j).equals("|")){
                        lastLocation = j+1;
                    }
                    else
                    {
                        lastLocation = cmd.size();
                    }
                    break;
                }
                else if( (j>0 && j==(cmd.size()-1) && cmd.get(j-1).equals("|")))
                {
                    if(j==cmd.size()-1 && j > 0 && cmd.get(j-1).equals("|"))
                    {
                        ArrayList<String> arr = new ArrayList<String>();
                        arr.add(cmd.get(j));
                        commands.set(i, arr);
                        lastLocation = cmd.size();
                    }
                    break;
                }
                else if((j==0 && cmd.size() > 1 && cmd.get(1).equals("|")))
                {
                    commands.set(i,new ArrayList<>(cmd.subList(j, j+1)));
                    lastLocation = j+2;
                    break;
                }
                else if(j>0 && j <(cmd.size()-1) && cmd.size() >= 3)
                {
                    if(cmd.get(j-1).equals("|") & cmd.get(j+1).equals("|"))
                    {
                        lastLocation=j+2;
                        commands.set(i,new ArrayList<>(cmd.subList(j, j+1)));
                        break;
                    }
                }
                if(cmd.size() > 2 && j==0)
                {
                    if(cmd.get(1).equals(">") || cmd.get(1).equals(">>"))
                    {
                        lastLocation = j+1;
                        commands.set(i,new ArrayList<>(cmd.subList(j, j+1)));
                        break;
                    }
                }


            }
        }

        for(int i=0; i<commands.size(); i++)
        {
            if(!commands.get(i).get(0).equals("args")) {
                if (!validateCommands(commands.get(i))) {
                    ArrayList<ArrayList<String>> obj = new ArrayList<ArrayList<String>>();
                    return obj;
                }
            }
        }

        return commands;
    }

    public boolean validateCommands(ArrayList<String> cmd)
    {
        boolean exists = false;

        for(int i=0; i<cmds.length; i++)
        {
            if(cmd.get(0).equalsIgnoreCase(cmds[i][0]) && cmd.size() == (1+ Integer.parseInt(cmds[i][1])))
            {
                exists = true;
                break;
            }
        }

        if(!exists)
        {
            return false;
        }

        if(cmd.size() > 1) {
            if (exists && PathCorrect(cmd.get(1)) && PathCorrect(cmd.get(cmd.size() - 1))) {
                cmd.set(1,AbsolutePath(cmd.get(1)));
                cmd.set(cmd.size()-1,AbsolutePath(cmd.get(cmd.size()-1)));
                cmd.set(0,cmd.get(0).toLowerCase());
                return true;
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    public boolean PathCorrect(String arg)
    {
        int start = 0;
        if((arg.charAt(1) == ':' && arg.charAt(2) =='/') || (arg.charAt(1) == ':' && arg.charAt(2) =='\\'))
        {
            start = 3;
        }
        for(int i=start; i<arg.length()-1; i++)
        {
            if(arg.charAt(i) == '<' || arg.charAt(i) == '>' || arg.charAt(i) == '*' || arg.charAt(i) == ':' || arg.charAt(i) == '"' || arg.charAt(i) == '?' || arg.charAt(i) == '|' || (arg.charAt(i) == '\\' && arg.charAt(i+1) == '\\' ) || (arg.charAt(i) == '/' && arg.charAt(i+1) == '/') )
            {
                return false;
            }
        }
        return true;
    }

    public String AbsolutePath(String arg)
    {
        String Dir = Obj.getWorkDirectory();
        if(arg.equals(".."))
        {
            return arg;
        }
        else if(arg.charAt(1) != ':')
        {
            if(arg.charAt(arg.length()-1) != '\\' && arg.charAt(arg.length()-1) != '/' && (Dir.charAt(Dir.length()-1) == '\\' || Dir.charAt(Dir.length()-1) == '/'))
            {
                return Dir+arg+"\\";
            }
            else if((Dir.charAt(Dir.length()-1) != '\\' && arg.charAt(0) != '\\') || (Dir.charAt(Dir.length()-1) != '/' && arg.charAt(0) != '/') )
            {
                if(arg.charAt(arg.length()-1) != '\\' && arg.charAt(arg.length()-1) != '/')
                {
                    return Dir+'\\'+arg+'\\';
                }
                return Dir+'\\'+arg;
            }
            return Obj.getWorkDirectory()+arg;
            //return relative + abs path
        }
        return arg;
    }
}
