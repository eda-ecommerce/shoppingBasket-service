# Sending the whole entity for shopping basket, instead of single items?

**Status** : Proposed

**Context**

If the customer puts an item in the shopping basket, we could send the whole shopping basket entity via Kafka, 
or send single items. This would reduce the amount of listeners to be implemented, but would also increase the payload size.

**Discussion**

Pro:

- Less listeners to be implemented
- Consistent with similar events
- Listener logic can be simpler, as in "Update the whole basket" and not "Update Basket details", "Add item", "Remove item" etc

Con:

- Customers with a big shopping basket, who frequently add or remove items, would generate a lot of traffic

**Decision**

To be determined