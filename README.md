# uuid
Java UUID generators. Random UUIDv4 and a special epoch UUID for datastores that like 'em roughly clustered in insert/time order.

# Random UUIDv4
The one in [java.util.UUID](http://docs.oracle.com/javase/7/docs/api/java/util/UUID.html#randomUUID()) uses Java's SecureRandom.
On Linux it is known to exhaust the entropy pool, hanging up the process.

This one is much faster, still has pretty good security characteristics and never hangs on Linux's entropy pool.

# Epoch UUID
Uses MAC addresses, a random number, timestamp and a counter, runs it through SHA1 and gets 12 bytes from the resulting hash. 

Finally it prepends a MSB first timestamp in seconds since the epoch. It's unsigned and will wrap in 2106.

# UUID class
The string operations generate less garbage, making UUID heavy code more efficient.

The compareTo function compares UUIDs correctly in byte-lexicographic order. As a side-effect the UUID comparison
also matches the string comparison.

It is a drop-in replacement for java UUID in most cases, supporting methods by the same name.

# Speed
random: about 5e6/s

epoch: about 1.8e6/s

on a 2.4GHz Core i5.

# See also
[Java UUID Generator](https://github.com/cowtowncoder/java-uuid-generator).
