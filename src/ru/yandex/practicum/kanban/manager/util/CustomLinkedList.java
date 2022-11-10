package ru.yandex.practicum.kanban.manager.util;

import ru.yandex.practicum.kanban.task.Task;

import java.util.ArrayList;

public class CustomLinkedList<T> {
    private int size = 0;
    private Node<T> first;
    private Node<T> last;


    public Node<T> linkLast(T element) {
        Node<T> l = last;
        Node<T> newNode = new Node<>(element, l, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;

        return newNode;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node<T> currentNode = first;
        if(currentNode != null){
            while(currentNode != null) {
                tasks.add((Task) currentNode.task);
                currentNode = currentNode.next;
            }
        }

        return tasks;
    }

    public void removeNode(Node element) {
        for (Node<T> x = first; x != null; x = x.next) {
            if (element.task != null && element.task.equals(x.task)) {
                final Node<T> next = x.next;
                final Node<T> prev = x.prev;

                if (prev == null) {
                    first = next;
                } else {
                    prev.next = next;
                    x.prev = null;
                }

                if (next == null) {
                    last = prev;
                } else {
                    next.prev = prev;
                    x.next = null;
                }

                x.task = null;
                size--;
            }
        }
    }
}
