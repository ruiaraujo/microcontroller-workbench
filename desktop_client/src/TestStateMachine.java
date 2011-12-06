import java.util.Arrays;
import java.util.List;

import com.doublecheck.bstworkbench.compiler.Instruction;
import com.doublecheck.bstworkbench.compiler.commands.TapStateMachine;


public class TestStateMachine {

    /**
     * @param args
     */
    public static void main(String[] args) {
        TapStateMachine machine = TapStateMachine.getInstance();
        List<Instruction> list  = machine.moveToState("shift-ir");
        System.out.println(Arrays.toString(list.toArray()));
        list  = machine.moveToState("shift-ir");
        System.out.println(Arrays.toString(list.toArray()));
        list  = machine.moveToState("shift-dr");
        System.out.println(Arrays.toString(list.toArray()));
    }

}
