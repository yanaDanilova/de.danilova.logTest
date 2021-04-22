import java.util.*;

public class ArraysApp {

    public int[] changeArr(int[] arr) {
        List<Integer> intList = new ArrayList<>(arr.length);
        for (int i : arr) {
            intList.add(i);
        }
        int indexLast4 = intList.lastIndexOf(4);
        if (indexLast4 == -1) {
            throw new RuntimeException();
        }
        List<Integer> nwIntList = intList.subList(indexLast4 + 1, intList.size());
        int[] nwArr = new int[nwIntList.size()];
        for (int i = 0; i < nwIntList.size(); i++) {
            nwArr[i] = nwIntList.get(i);
        }
        return nwArr;
    }


    public boolean checkArr(int[] arr) {
        boolean one = false;
        boolean four = false;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] == 1) {
                one = true;
                }
            if(arr[i] == 4){
                four = true;
            }
            if(arr[i] != 1 && arr[i] != 4){
                return false;
            }
            }

        return one && four;
    }
    }



