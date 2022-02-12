import java.util.*;
import java.util.stream.Collectors;

public class TaskB {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String inputText = sc.nextLine();
        inputText = inputText.replace(" ", "");
        inputText = inputText.replace("\n", "");
        inputText = inputText.replace("\r", "");

        List<String> alf = Arrays.asList("10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33".split(" "));
        ArrayList<String> names = getNames(inputText);
        alf = alf.subList(0, names.size());

        for (int i = 0; i < names.size(); i++){
            inputText = inputText.replace(names.get(i), alf.get(i));
        }


        List<Character> c = inputText.chars().mapToObj(x -> (char) x).collect(Collectors.toList());

        MyTree tree = calc(c);
        int countTrue = 0;
        int countFalse = 0;
        boolean[] params = new boolean[alf.size()];

        if (calcFromTree(tree, alf, params))
            countTrue++;
        else
            countFalse++;

        for (int i = 1; i < Math.pow(2, alf.size()); i++){
            int j = 0;
            while (j < alf.size() && params[j] == true){
                params[j] = false;
                j++;
            }
            params[j] = true;

            if (calcFromTree(tree, alf, params))
                countTrue++;
            else
                countFalse++;
        }



        if (countTrue == 0)
            System.out.println("Unsatisfiable");
        else if (countFalse == 0)
            System.out.println("Valid");
        else
            System.out.println("Satisfiable and invalid, " + countTrue + " true and " + countFalse + " false cases");
    }

    static boolean calcFromTree(MyTree tree, List<String> alf, boolean[] params){
        boolean result;
        boolean left;
        boolean right;

        if (tree.number)
            result = params[alf.indexOf(tree.value)];
        else if (tree.value.equals("&")){
             left = calcFromTree(tree.left, alf, params);
             right = calcFromTree(tree.right, alf, params);
            result = left & right;
        }else if (tree.value.equals("|")){
             left = calcFromTree(tree.left, alf, params);
             right = calcFromTree(tree.right, alf, params);
            result = left | right;
        }else{
             left = calcFromTree(tree.left, alf, params);
            right = calcFromTree(tree.right, alf, params);
            result = !left | right;
        }

        if (tree.otr)
            result = !result;

        return result;
    }

    public static ArrayList<String> getNames(String inputText){
        char[] text = inputText.toCharArray();
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < text.length; i++) {
            String name = "";
            if (Character.isLetter(text[i])) {
                name += text[i];
                while (i + 1 < text.length && (Character.isLetter(text[i + 1]) || Character.isDigit(text[i + 1]) || text[i + 1] == '\'')) {
                    name += text[i + 1];
                    i++;
                }
                if (!names.contains(name))
                    names.add(name);
            }
        }
        ExampleComparator xx = new ExampleComparator();
        names.sort(xx);
        return names;
    }

    static int getCloseRight(List<Character> c){
        int count = 0;
        int i = 1;
        while (i < c.size()){
            if ( c.get(i) == ')' && count == 0)
                return i;
            if (c.get(i) == '(')
                count++;
            if (c.get(i) == ')')
                count--;
            i++;
        }
        return i;
    }

    static int findImpl(List<Character> c){
        int count = 0;
        for (int i = 0; i < c.size(); i++){
            if (c.get(i) == '(')
                count++;
            if (c.get(i) == ')')
                count--;
            if (c.get(i) == '-' && count == 0)
                return i;
        }
        return -1;
    }

    static int findOr(List<Character> c){
        int count = 0;
        int result = -1;
        for (int i = 0; i < c.size(); i++){
            if (c.get(i) == '(')
                count++;
            if (c.get(i) == ')')
                count--;
            if (c.get(i) == '|' && count == 0)
                return i;
        }
        return result;
    }

    static int findAnd(List<Character> c){
        int count = 0;
        int result = -1;
        for (int i = 0; i < c.size(); i++){
            if (c.get(i) == '(')
                count++;
            if (c.get(i) == ')')
                count--;
            if (c.get(i) == '&' && count == 0)
                return i;
        }
        return result;
    }

    static List<Character> removeSkob(List<Character> c){
        while (c.get(0) == '(' && getCloseRight(c) == c.size()-1){
            c.remove(0);
            c.remove(c.size()-1);
        }
        return c;
    }

    public static MyTree calc(List<Character> x){
        MyTree result = new MyTree(0);
        List<Character> c = new ArrayList<>(x);
        c = removeSkob(c);

        boolean otr = false;
        while (c.get(0) == '!'){
            if (c.get(1) == '!'){
                c.remove(0);
                c.remove(0);
                c = removeSkob(c);
            }else if (c.size() == 3){
                c.remove(0);
                otr = !otr;
            }else if (c.get(1) == '(' && getCloseRight(c.subList(1, c.size())) == c.size() - 2){
                c.remove(0);
                c.remove(0);
                c.remove(c.size() - 1);
                c = removeSkob(c);
                otr = !otr;
            }else{
                break;
            }
        }

        result.setOtr(otr);

        if (findImpl(c) != -1){
            result.setValue("-");
            result.setNumber(false);
            result.setLeft(calc(c.subList(0, findImpl(c))));
            result.setRight(calc(c.subList(findImpl(c) + 2, c.size())));
            return result;
        }else if (findOr(c) != -1){
            result.setNumber(false);
            result.setValue("|");
            result.setLeft(calc(c.subList(0, findOr(c))));
            result.setRight(calc(c.subList(findOr(c) + 1, c.size())));
            return result;
        }else if (findAnd(c) != -1){
            result.setNumber(false);
            result.setValue("&");
            result.setLeft(calc(c.subList(0, findAnd(c))));
            result.setRight(calc(c.subList(findAnd(c) + 1, c.size())));
            return result;
        }else if (c.size() == 2){
            result.setNumber(true);
            result.setValue(String.valueOf(c.get(0)) + String.valueOf(c.get(1)));
            return result;
        }

        throw new RuntimeException("Error");
    }



    public static class ExampleComparator  implements Comparator<String> {
        public int compare(String obj1, String obj2) {
            return -Integer.compare(obj1.length(), obj2.length());
        }
    }

}
