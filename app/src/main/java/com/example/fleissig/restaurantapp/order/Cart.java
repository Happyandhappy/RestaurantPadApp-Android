package com.example.fleissig.restaurantapp.order;

import android.content.Context;

import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.DishOrder;
import com.example.fleissig.mylibrary.Order;
import com.example.fleissig.mylibrary.PayingOrder;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    List<CartItem> items = new ArrayList<>();
    List<CartItem> orderedItems = new ArrayList<>();
    List<PayingOrder> pays = new ArrayList<>();
    public String orderId;


    public void createNewOrder(String orderId) {
        items = new ArrayList<>();
        orderedItems = new ArrayList<>();
        pays = new ArrayList<>();
        this.orderId = orderId;
    }

    public List<PayingOrder> getPays() {
        return pays;
    }

    public void setPays(List<PayingOrder> pays) {
        this.pays = pays;
    }

    public void loadOrder(Context context, String orderId, Order order, Map<String, Dish> dishes) {
        items.clear();
        this.orderId = orderId;
        String restaurantId = ShareFunctions.readRestaurantId(context);

        Map<String, DishOrder> mapOrder = order.getPreorderDishes();
        for (String dishId : mapOrder.keySet()) {
            DishOrder dishOrder = mapOrder.get(dishId);
            Dish dish = dishes.get(dishId);
            CartItem newItem = new CartItem(dishId, dish, dishOrder, dishOrder.getQuantity());
            items.add(newItem);
        }
    }


    public double getTotalPrice() {
        double totalPrice = .0;

        for (CartItem item : items) {
            Dish dish = item.getDish();
            DishOrder dishOrder = item.getDishOrder();
            long quantity = dishOrder.getQuantity();
            Double price = getPrice(dish);
            totalPrice += price * quantity;
        }

        return totalPrice;
    }


    private Double getPrice(Dish dish) {
        if (dish.price != null) {
            return dish.price;
        } else if (dish.price_bottle != null) {
            return dish.price_bottle;
        }
        return 0.0;
    }

    public int getTotalCount() {
        int sum = 0;
        for (CartItem item : items) {
            int quantity = item.getDishOrder().getQuantity();
            sum += quantity;
        }

        return sum;
    }

    public int getTotalOrderCount() {
        int sum = 0;
        for (CartItem item : items) {
            int quantity = item.getOrderedQuantity();
            sum += quantity;
        }

        return sum;
    }

    public CartItem findCartItem(String dishId) {
        for (CartItem item : items) {
            if (item.getDishId().equals(dishId)) {
                return item;
            }
        }
        return null;
    }

    public CartItem findOrderedCartItem(String dishId) {
        for (CartItem item : orderedItems) {
            if (item.getDishId().equals(dishId)) {
                return item;
            }
        }
        return null;
    }

    private CartItem findCartItem(Dish dish) {
        for (CartItem item : items) {
            if (item.getDish().equals(dish)) {
                return item;
            }
        }
        return null;
    }

    public void setDish(DatabaseReference dishRef, Dish dish, int quantity) {
        String dishId = dishRef.getKey();
        CartItem item = findCartItem(dishId);
        if (item != null) {
            DishOrder dishOrder = item.getDishOrder();
            dishOrder.setQuantity(quantity);
        } else {
            DishOrder dishOrder = new DishOrder();
            dishOrder.setQuantity(quantity);
            dishOrder.setSinglePrice(getPrice(dish));

            CartItem newItem = new CartItem(dishId, dish, dishOrder);
            items.add(newItem);
        }
    }

    public boolean addDish(String dishId, Dish dish, int quantity) {
        CartItem item = findCartItem(dishId);
        if (item != null) {
            DishOrder dishOrder = item.getDishOrder();
            dishOrder.setQuantity(dishOrder.getQuantity() + quantity);
            return true;
        } else {
            DishOrder dishOrder = new DishOrder();
            dishOrder.setQuantity(quantity);
            dishOrder.setSinglePrice(getPrice(dish));

            CartItem newItem = new CartItem(dishId, dish, dishOrder);
            items.add(newItem);
            return false;
        }
    }

    public boolean addDish(DatabaseReference dishRef, Dish dish, int quantity) {
        String dishId = dishRef.getKey();
        return addDish(dishId, dish, quantity);
    }

    public void addDish(DatabaseReference dishRef, Dish dish) {
        String dishId = dishRef.getKey();
        CartItem item = findCartItem(dishId);
        if (item != null) {
            DishOrder dishOrder = item.getDishOrder();
            int quantity = dishOrder.getQuantity();
            quantity = quantity + 1;
            dishOrder.setQuantity(quantity);
        } else {
            DishOrder dishOrder = new DishOrder();
            dishOrder.setQuantity(1);
            dishOrder.setSinglePrice(getPrice(dish));

            CartItem newItem = new CartItem(dishId, dish, dishOrder);
            items.add(newItem);
        }
    }

    public Map<String, DishOrder> makeOrder() {
        Map<String, DishOrder> returnMap = new HashMap<>();
        for (CartItem orderedItem : orderedItems) {
            DishOrder dishOrder = orderedItem.getDishOrder();
            orderedItem.setOrderedQuantity(dishOrder.getQuantity());
            returnMap.put(orderedItem.getDishId(), dishOrder);
        }

        return returnMap;
    }

    public void addOrderedPayment(PayingOrder amount) {
        pays.add(amount);
    }

    public void addToOrdered() {
        for (CartItem item : items) {
            CartItem cartItem = findOrderedCartItem(item.dishId);
            if (cartItem != null) {
                cartItem.setOrderedQuantity(cartItem.getOrderedQuantity() + item.getDishOrder().getQuantity());
                cartItem.getDishOrder().setQuantity(cartItem.getOrderedQuantity());
            } else {
                DishOrder dishOrder = item.getDishOrder();
                item.setOrderedQuantity(dishOrder.getQuantity());
                orderedItems.add(item);
            }
        }
        items.clear();
    }

    public void addToOrdered(String dishId, Dish dish, int quantity) {

        CartItem cartItem = findOrderedCartItem(dishId);
        if (cartItem != null) {
            cartItem.setOrderedQuantity(cartItem.getOrderedQuantity() + quantity);
        } else {
            DishOrder dishOrder = new DishOrder();
            dishOrder.setQuantity(quantity);
//            dishOrder.setOrderedQuantity(0);
            dishOrder.setSinglePrice(getPrice(dish));

            CartItem newItem = new CartItem(dishId, dish, dishOrder);

//            dishOrder.setOrderedQuantity(dishOrder.getQuantity());
            newItem.setOrderedQuantity(dishOrder.getQuantity());
            orderedItems.add(newItem);
        }

    }

    public List<CartItem> getItems() {
        return items;
    }

    public List<CartItem> getOrderedItems() {
        return orderedItems;
    }

    public void setDishQuanlity(String dishId, int quanlity) {
        CartItem item = findCartItem(dishId);
        if (item != null) {
            item.getDishOrder().setQuantity(quanlity);
        }
    }

    public void setDishQuanlity(Dish dish, int quanlity) {
        CartItem item = findCartItem(dish);
        if (item != null) {
            item.getDishOrder().setQuantity(quanlity);
        }
    }

    public void removeDish(Dish dish) {
        CartItem item = findCartItem(dish);
        if (item != null) {
            items.remove(item);
        }
    }

    public void incrementDish(Dish dish) {
        CartItem item = findCartItem(dish);
        if (item != null) {
            item.incrementQuantity();
        }
    }

    public void decrementDish(Dish dish) {
        CartItem item = findCartItem(dish);
        if (item != null) {
            item.decrementQuantity();
            boolean isZero = item.isZero();
            if (isZero) {
                items.remove(item);
            }
        }
    }
}
