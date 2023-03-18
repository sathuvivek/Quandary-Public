package interpreter;

import java.util.ArrayList;
import java.util.Objects;

public class CustomArrayList<K> extends ArrayList<K> {
    private int alreadyHashed;
    @Override
    public int hashCode() {
        if(alreadyHashed != 0)
            return alreadyHashed;
        int result = 1;
        for (K element : this) {
           // System.out.println("Element : " + element.toString());
            result = 31 * result + element.hashCode();
        }
        alreadyHashed = result;
        return result;
       // return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
     //   System.out.println("Comparing custom lists");
        if (this == o) {
            return true;
        }
      //  System.out.println("Instance didnt match");
        if (!(o instanceof CustomArrayList)) {
            return false;
        }
       // System.out.println("Correct instance");
        CustomArrayList<?> that = (CustomArrayList<?>) o;
        if (this.size() != that.size()) {
            return false;
        }
       // System.out.println("Size matched");
        for (int i = 0; i < this.size(); i++) {
            if(this.get(i) != null && that.get(i) != null) {
                if (!this.get(i).equals(that.get(i))) {
                  //  System.out.println("Exact equality failed");
                    return false;
                }
            }
        }
        return true;
    }
}
