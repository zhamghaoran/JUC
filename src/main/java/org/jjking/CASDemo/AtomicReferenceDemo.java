package org.jjking.CASDemo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
@Data
@NoArgsConstructor
class User {
    String name;
    int age;
}

public class AtomicReferenceDemo {
    public static void main(String[] args) {
        AtomicReference<User> userAtomicReference = new AtomicReference<User>();
        User z3 = new User("z3", 22);
        User l4 = new User("l4", 28);
        userAtomicReference.set(z3);
        System.out.println(userAtomicReference.compareAndSet(z3, l4) + "   " + userAtomicReference.get().toString());
        System.out.println(userAtomicReference.compareAndSet(z3, l4) + "   " + userAtomicReference.get().toString());


    }
}
