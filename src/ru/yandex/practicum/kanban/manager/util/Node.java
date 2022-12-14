package ru.yandex.practicum.kanban.manager.util;

import java.util.Objects;

public class Node<E> {
    protected Node<E> prev;
    protected Node<E> next;
    protected E task;

    public Node(E task, Node<E> prev, Node<E> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(prev, node.prev) && Objects.equals(next, node.next) && Objects.equals(task, node.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prev, next, task);
    }
}
