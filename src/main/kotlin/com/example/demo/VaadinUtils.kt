package com.example.demo

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.text
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant

fun <T> HasComponents.addAndGet(component: T): T where T : Component {
    add(component)
    return component
}

fun notification(closure: Notification.(Notification) -> Unit): Notification {
    val notification = Notification()
    closure(notification, notification)
    return notification;
}

fun errorNotification(text: String, closure: Notification.(Notification) -> Unit = {}) = notification { notification ->
    addThemeVariants(NotificationVariant.LUMO_ERROR)
    text(text)
    button {
        icon = Icon("lumo", "cross")
        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
        setAriaLabel("close")
        onLeftClick {
            notification.close()
        }
    }
    closure(notification, notification)
}

fun HasComponents.button(text: String, block: (@VaadinDsl Button).(Button) -> Unit): Button {
    val button = this.button(text)
    block(button, button)
    return button
}

fun Component.findChild(predicate: (Component) -> Boolean): Component? {
    for (child in this.children.toList()) {
        if (predicate(child)) {
            return child
        }
        val found = child.findChild(predicate)
        if (found!=null) {
            return found
        }
    }
    return null
}

private fun findChildImpl(component: Component, predicate: (Component) -> Boolean): Component? {
    for (child in component.children) {
        return if (predicate(child)) {
            child
        } else {
            findChildImpl(child, predicate)
        }
    }
    return null
}

