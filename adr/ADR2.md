# ADR #2 Fire event on each added item?

**Status** : Proposed

**Context**

If the customer puts an item in the shopping basket, we could fire an event called
"ItemAddedToBasket", which could be used by other systems to track the customer's shopping behaviour.


**Discussion**

- Covers future use cases. If we wanted to aggregate specific items, we would otherwise have to listen to the ShoppingBasket topic and filter the items
- adds traffic / complexity


**Decision**

TBD