package org.jjking.CASDemo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicStampedReference;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Book {
    private int id;
    private String bookName;
}

public class AtomicStampedDemo {
    public static void main(String[] args) {
        Book book = new Book(1, "javaBook");
        AtomicStampedReference<Book> bookAtomicStampedReference = new AtomicStampedReference<Book>(book, 1);
        System.out.println(bookAtomicStampedReference.getReference() + "    " + bookAtomicStampedReference.getStamp());
        Book book1 = new Book(2, "mysqlbook");

        boolean b = bookAtomicStampedReference.compareAndSet(book, book1, bookAtomicStampedReference.getStamp(), bookAtomicStampedReference.getStamp() + 1);

        System.out.println(bookAtomicStampedReference.getReference() + "   " + b);

    }
}
