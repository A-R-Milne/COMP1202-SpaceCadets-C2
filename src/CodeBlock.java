public class CodeBlock {
    private final int level;
    private final String varToCheck;
    private boolean vTCFlag = true;
    private int startOfLoop;

    public CodeBlock(int level, String varToCheck) {
        this.level = level;
        this.varToCheck = varToCheck;
    }

    public CodeBlock(int level, Packet packet) {
        this.level = level;
        this.varToCheck = String.valueOf(level);
        packet.checkForVar(varToCheck, false);
        packet.assign(varToCheck);
    }

    public Packet loop(Packet packet) {
        packet.checkForVar(varToCheck,false);
        setStartOfLoop(packet.getLineNumber());
        while (isvTCFlag()) {
            if (packet.getLineToRead() < packet.getCodeLength()) {
                String codeLine = packet.getLine();
                //check that code ends with semicolon
                if (codeLine.endsWith(";") || codeLine.startsWith("#")) {
                    //clear command
                    if (codeLine.startsWith("clear")) {
                        String varName = codeLine.substring(6, codeLine.length() - 1);
                        packet.checkForVar(varName, false);
                        packet.assign(varName);
                        //assign command
                    } else if (codeLine.startsWith("assign")) {
                        int commaPos = codeLine.indexOf(",");
                        String varName = codeLine.substring(7, commaPos);
                        String valueString = codeLine.substring(commaPos+2, codeLine.length()-1);
                        try {
                            float value = Integer.parseInt(valueString);
                            packet.checkForVar(varName, false);
                            packet.assign(varName,value);
                        } catch (NumberFormatException e) {
                            packet.checkForVar(varName, false);
                            packet.checkForVar(valueString, false);
                            packet.assign(varName, valueString);
                        }
                    //incr command
                    } else if (codeLine.startsWith("incr")) {
                        String varName = codeLine.substring(5, codeLine.length() - 1);
                        packet.checkForVar(varName, false);
                        packet.incr(varName);
                    //decr command
                    } else if (codeLine.startsWith("decr")) {
                        String varName = codeLine.substring(5, codeLine.length() - 1);
                        packet.checkForVar(varName, false);
                        packet.decr(varName);
                    //while _ not 0 do command    
                    } else if (codeLine.startsWith("while") && codeLine.endsWith("not 0 do;")) {
                        String varName = codeLine.substring(6, codeLine.length()-10);
                        packet.checkForVar(varName, true);
                        if (packet.getVarList().getVarValue(varName) != 0) {
                            CodeBlock loop = new CodeBlock(getLevel()+1, varName);
                            packet = loop.loop(packet);
                        }
                        else {
                            boolean skip = true;
                            int nestCount = 1;
                            while(skip) {
                                String lineCheck2 = packet.getLine();
                                if(lineCheck2.startsWith("if")) nestCount++;
                                if(lineCheck2.startsWith("end")) nestCount--;
                                if(nestCount==0) {
                                    skip = false;
                                    packet.getCodeList().jumpTo(packet.getLineNumber()-1);
                                }
                            }
                        }
                    //end command    
                    } else if (codeLine.startsWith("end")) {
                        if (packet.getVarList().getVarValue(getVarToCheck()) == 0) {
                            setVTCFlag(false);
                        } else {
                            packet.getCodeList().jumpTo(getStartOfLoop());
                        }
                    //basic operations
                    } else if (codeLine.startsWith("+") || codeLine.startsWith("-") || codeLine.startsWith("*") || codeLine.startsWith("/")) {
                        String instruction = codeLine.substring(0,1);
                        String allVars = codeLine.substring(2, codeLine.length()-1);
                        int commaPos1 = allVars.indexOf(",");
                        String endVars = allVars.substring(commaPos1+2);
                        int commaPos2 = endVars.indexOf(",");
                        String var1 = allVars.substring(0, commaPos1);
                        String var2 = endVars.substring(0, commaPos2);
                        String varOut = endVars.substring(commaPos2+2);
                        switch (instruction) {
                            case "+" -> packet.add(var1, var2, varOut);
                            case "-" -> packet.subtract(var1, var2, varOut);
                            case "*" -> packet.multiply(var1, var2, varOut);
                            case "/" -> packet.divide(var1, var2, varOut);
                        }
                    //else command
                    } else if (codeLine.startsWith("else")) {
                        setVTCFlag(false);
                    //if command
                    } else if (codeLine.startsWith("if")) {
                        String condition = codeLine.substring(3, codeLine.length()-1);
                        String comparison = "";
                        if(condition.contains("=")) comparison = "=";
                        else if(condition.contains("<")) comparison = "<";
                        else if(condition.contains(">")) comparison = ">";
                        int compPos = condition.indexOf(comparison);
                        Variable var1 = toVariable(condition.substring(0, compPos-1),packet);
                        Variable var2 = toVariable(condition.substring(compPos+2),packet);
                        boolean ifStatement = switch (comparison) {
                            case "=" -> (var1.getValue() == var2.getValue());
                            case "<" -> (var1.getValue() < var2.getValue());
                            case ">" -> (var1.getValue() > var2.getValue());
                            default -> false;
                        };
                        boolean skip;
                        String continueString;
                        if(ifStatement) {
                            CodeBlock loop = new CodeBlock(getLevel()+1, packet);
                            packet = loop.loop(packet);
                            packet.getCodeList().jumpTo(packet.getLineNumber()-1);
                            String lineCheck = packet.getLine();
                            skip = !lineCheck.equals("end");
                            continueString = "end";
                        } else {
                            skip = true;
                            continueString = "else";
                        }
                        int nestCount = 1;
                        while(skip) {
                            String lineCheck2 = packet.getLine();
                            if(lineCheck2.startsWith("if")) nestCount++;
                            if(lineCheck2.startsWith(continueString)) nestCount--;
                            if(nestCount==0) {
                                skip = false;
                                packet.getCodeList().jumpTo(packet.getLineNumber()-1);
                                if(continueString.equals("else")) {
                                    CodeBlock loop = new CodeBlock(getLevel()+1, packet);
                                    packet = loop.loop(packet);
                                }
                            }
                        }
                    //if not a comment, command not valid
                    } else if(!codeLine.startsWith("#")) {
                        System.out.println("Error (3): Command " + codeLine + " not recognised. Please check the spelling of your command, and that the 'not 0 do' section of a while command is present.");
                        System.exit(0);
                    }
                //no semicolon at end of command    
                } else {
                    System.out.println("Error (2): Line of code does not end in ';'.");
                    System.exit(0);
                }
            } else {
                if (this.getLevel() != 0) {
                    System.out.println("Error (1): At least one 'while _ not 0 do' command does not have a corresponding 'end' command.");
                    System.exit(0);
                }
                break;
            }
        }
        return packet;
    }

    private Variable toVariable(String substring, Packet packet) {
        packet.checkForVar(substring,false);
        return packet.getVarList().getVarList().get(substring);
    }

    public int getLevel() {
        return level;
    }

    public String getVarToCheck() {
        return varToCheck;
    }

    public boolean isvTCFlag() {
        return vTCFlag;
    }

    public void setVTCFlag(boolean vTCFlag) {
        this.vTCFlag = vTCFlag;
    }

    public int getStartOfLoop() {
        return startOfLoop;
    }

    public void setStartOfLoop(int startOfLoop) {
        this.startOfLoop = startOfLoop;
    }
}