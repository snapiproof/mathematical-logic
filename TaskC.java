import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        input = input.replace(" ", "");
        input = input.replace("\n", "");
        input = input.replace("\t", "");

        String[] inputSplit = input.split("\\|-");
        String[] left = inputSplit[0].split(",");
        String A = left[left.length - 1];
        String newLeft;

        if (left.length == 1)
            newLeft = "";
        else
            newLeft = inputSplit[0].substring(0, inputSplit[0].length() - ("," + A).length());

        ArrayList<MyTree> listTrees = new ArrayList<>();
        ArrayList<String> list = new  ArrayList<>();
        int f = 0;
        while(sc.hasNextLine()){
            String string = sc.nextLine();
            string = string.replace(" ", "");
            string = string.replace("\t", "");
            string = string.replace("\n", "");
            list.add(f, string);
            List<Character> st = string.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
            listTrees.add(f, calc(st));
            f++;

            if (string.equals(inputSplit[1]))
                break;
        }
        String[] g = newLeft.split(",");
        List<Character> as = A.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
        MyTree treeA = calc(as);
        ArrayList<MyTree> listHypoteses;
        if (newLeft.equals("")) {
            listHypoteses = new ArrayList<>(0);
        }else{
            listHypoteses = new ArrayList<>(g.length);
            for (int m = 0; m < g.length; m++){
                listHypoteses.add(calc(g[m].chars().mapToObj(x -> (char) x).collect(Collectors.toList())));
            }
        }




        System.out.println(newLeft + "|- (" + A + ") -> (" + inputSplit[1] + ")");
        for (int i = 0; i < list.size(); i++){
            String string = list.get(i);


            int[] l = isModusPonens(listTrees, i);
            if (l[0] != -1){
                System.out.printf("((%s) -> (%s)) -> (((%s) -> ((%s) -> (%s))) -> ((%s) -> (%s)))\n", A, list.get(l[0]), A, list.get(l[0]), string, A, string);
                System.out.printf("((%s) -> ((%s) -> (%s))) -> ((%s) -> (%s))\n", A, list.get(l[0]), string, A, string);
                System.out.printf("(%s) -> (%s)\n", A, string);
//                System.out.println("(" + (i+1) + ") " + string + " --- M.P. " + (l[0] + 1) + ", " + (l[1] + 1));
                continue;
            }
            int ax = isAxiom(listTrees.get(i));
            if (ax != -1){
                System.out.printf("(%s)\n", string);
                System.out.printf("(%s) -> ((%s) -> (%s))\n", string, A, string);
                System.out.printf("(%s) -> (%s)\n", A, string);
//                System.out.println("(" + (i+1) + ") " + string + " --- Ax. sch. " + ax);
                continue;
            }

            if (treeA.isEqual(listTrees.get(i))){

                System.out.printf("(%s) -> ((%s) -> (%s))\n", A, A, A);
                System.out.printf("(%s) -> (((%s) -> (%s)) -> (%s))\n", A, A, A, A);
                System.out.printf("((%s) -> ((%s) -> (%s))) -> (((%s) -> (((%s) -> (%s)) -> (%s))) -> ((%s) -> (%s)))\n", A, A, A, A, A, A, A, A, A);
                System.out.printf("((%s) -> (((%s) -> (%s)) -> (%s))) -> ((%s) -> (%s))\n", A, A, A, A, A, A);
                System.out.printf("(%s) -> (%s)\n", A, A);
//                System.out.println("(" + (i+1) + ") " + string + " --- Alpha ");
                continue;
            }

            if (isG(listHypoteses, listTrees.get(i))){
                System.out.printf("(%s)\n", string);
                System.out.printf("(%s) -> ((%s) -> (%s))\n", string, A, string);
                System.out.printf("(%s) -> (%s)\n", A, string);
//                System.out.println("(" + (i+1) + ") " + string + " --- Hyp. ");
                continue;
            }



//            System.out.println("(" + (i+1) + ")");
            throw new RuntimeException();
        }
    }

    public static int[] isModusPonens(ArrayList<MyTree> list, int i){
        int[] mp = new int[2];
        for (int j = i - 1; j >= 0; j--){
            if (list.get(j).getNegation() == 0 && list.get(j).getType().equals(MyTree.Type.IMPLICATION) && list.get(i).isEqual(list.get(j).getRight())){
                for (int l = i - 1; l >= 0; l--){
                    if (list.get(j).getLeft().isEqual(list.get(l))){
                        mp[0] = l;
                        mp[1] = j;
                        return mp;
                    }
                }
            }
        }
        mp[0] = -1;
        mp[1] = -1;
        return mp;
    }

    static int isAxiom(MyTree tree){
        if (!tree.getType().equals(MyTree.Type.IMPLICATION))
            return -1;

        /* 1, 2, 3, 8, 9 axioms */
        if (tree.getRight().getType().equals(MyTree.Type.IMPLICATION)){

            MyTree left = tree.getLeft();
            MyTree middle = tree.getRight().getLeft();
            MyTree right = tree.getRight().getRight();


            /* 1 axiom */
            if (left.isEqual(right) && tree.getNegation() == 0 && tree.getRight().getNegation() == 0)
                return 1;

            /* 2 axiom */
            if (left.getType().equals(MyTree.Type.IMPLICATION) &&
                    right.getType().equals(MyTree.Type.IMPLICATION) &&
                    middle.getType().equals(MyTree.Type.IMPLICATION) &&
                    middle.getRight().getType().equals(MyTree.Type.IMPLICATION) &&
                    left.getLeft().isEqual(middle.getLeft()) &&
                    left.getLeft().isEqual(right.getLeft()) &&
                    left.getRight().isEqual(middle.getRight().getLeft()) &&
                    middle.getRight().getRight().isEqual(right.getRight()) &&
                    tree.getNegation() == 0 && tree.getRight().getNegation() == 0 && tree.getLeft().getNegation() == 0 &&
                    middle.getNegation() == 0 && right.getNegation() == 0 && middle.getRight().getNegation() == 0)
                return 2;

            /* 3 axiom */
            if (right.getType().equals(MyTree.Type.AND) && left.isEqual(right.getLeft()) && right.getNegation() == 0 &&
            tree.getNegation() == 0 && tree.getRight().getNegation() == 0 && right.getNegation() == 0)
                return 3;

            /* 8 axiom */
            if (middle.getType().equals(MyTree.Type.IMPLICATION) && right.getType().equals(MyTree.Type.IMPLICATION) && right.getLeft().getType().equals(MyTree.Type.OR)
                    && left.getType().equals(MyTree.Type.IMPLICATION) && left.getRight().isEqual(middle.getRight()) &&
                    left.getRight().isEqual(right.getRight()) && middle.getNegation() == 0 &&
            right.getNegation() == 0 && right.getLeft().getNegation() == 0 && tree.getNegation() == 0
            && left.getNegation() == 0 && tree.getRight().getNegation() == 0){
                if (right.getLeft().getRight().isEqual(left.getLeft()) && right.getLeft().getLeft().isEqual(middle.getLeft())
                || right.getLeft().getRight().isEqual(middle.getLeft()) && right.getLeft().getLeft().isEqual(left.getLeft()))
                    return 8;
            }

            /* 9 axiom */
            if (middle.getType().equals(MyTree.Type.IMPLICATION) && left.getType().equals(MyTree.Type.IMPLICATION) &&
                    left.getLeft().isEqual(middle.getLeft()) && left.getLeft().isNegation(right)
                    && left.getRight().isNegation(middle.getRight()) && middle.getNegation() == 0 && tree.getNegation() == 0
                && left.getNegation() == 0 && tree.getRight().getNegation() == 0)
                return 9;
        }


        /* 4, 5, 6, 7, 10 axioms */
        /* 10 axiom */
        if (tree.getLeft().isDoubleNegation(tree.getRight()) && tree.getNegation() == 0
        && tree.getLeft().getNegation() - tree.getRight().getNegation() == 2)
            return 10;

        /* 4, 5 axiom */
        if (tree.getLeft().getType().equals(MyTree.Type.AND) && tree.getNegation() == 0
                && tree.getLeft().getNegation() == 0){
            if (tree.getLeft().getLeft().isEqual(tree.getRight()))
                return 4;
            if (tree.getLeft().getRight().isEqual(tree.getRight()))
                return 5;
        }

        /* 6, 7 axiom */
        if (tree.getRight().getType().equals(MyTree.Type.OR) && tree.getNegation() == 0
                && tree.getRight().getNegation() == 0){
            if (tree.getRight().getLeft().isEqual(tree.getLeft()))
                return 6;
            if (tree.getRight().getRight().isEqual(tree.getLeft()))
                return 7;
        }

        return -1;
    }

    public static boolean isG(ArrayList<MyTree> hyp, MyTree tree){
        if (hyp.size() == 0) return false;
        for (MyTree h: hyp) {
            if (h.isEqual(tree))
                return true;
        }
        return false;
    }

    static int getCloseRight(List<Character> c){
        int count = 0;
        int i = 1;
        while (i < c.size()){
            if (c.get(i) == ')' && count == 0)
                return i;
            if (c.get(i) == '(')
                count++;
            if (c.get(i) == ')')
                count--;
            i++;
        }
        return i;
    }

    static int findImplication(List<Character> c){
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
                result = i;
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
                result = i;
        }
        return result;
    }

    static List<Character> removeSkob(List<Character> x){
        List<Character> c = new ArrayList<>(x);

        while (c.get(0) == '(' && getCloseRight(c) == c.size()-1){
            c.remove(0);
            c.remove(c.size()-1);
        }
        return c;
    }

    public static MyTree calc(List<Character> x){
        MyTree result = new MyTree();
        List<Character> c = new ArrayList<>(x);
        c = removeSkob(c);
        int negation = 0;
        int i = 0;
        result.setNegation(negation);
        while (c.get(i) == '!'){
            if (c.get(i+1) == '(' && getCloseRight(c.subList(i+1, c.size())) == c.size() - 2 - i){
                c = c.subList(i+2, c.size() - 1);
                c = removeSkob(c);
                negation += i + 1;
                result.setNegation(negation);
                i = 0;
                continue;
            }
            i++;
        }
        if (findImplication(c) != -1){
            result.setType(MyTree.Type.IMPLICATION);
            result.setLeft(calc(c.subList(0, findImplication(c))));
            result.setRight(calc(c.subList(findImplication(c) + 2, c.size())));
            return result;
        }else if (findOr(c) != -1){
            result.setType(MyTree.Type.OR);
            result.setLeft(calc(c.subList(0, findOr(c))));
            result.setRight(calc(c.subList(findOr(c) + 1, c.size())));
            return result;
        }else if (findAnd(c) != -1){
            result.setType(MyTree.Type.AND);
            result.setLeft(calc(c.subList(0, findAnd(c))));
            result.setRight(calc(c.subList(findAnd(c) + 1, c.size())));
            return result;
        }else {
            result.setType(MyTree.Type.VAR);
            int negationVar = 0;
            while (c.get(0) == '!'){
                c.remove(0);
                negationVar++;
                removeSkob(c);
            }
            StringBuilder sr = new StringBuilder();
            for (char v : c) {
                sr.append(v);
            }
            result.setNegation(result.getNegation() + negationVar);
            result.setValue(sr.toString());
            return result;
        }
    }
}
