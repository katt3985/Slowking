package project.slowking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pair<A,B> {

    private A a;
    private B b;

    public static <A,B> Pair<A,B> of(A a, B b){
        return new Pair<>(a,b);
    }

}
