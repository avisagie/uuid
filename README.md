# uuid
Java UUID generators. Random UUIDv4 and a special epoch UUID for datastores that like 'em roughly clustered in insert order.

# Random UUIDv4
The one in [java.util.UUID](http://docs.oracle.com/javase/7/docs/api/java/util/UUID.html#randomUUID()) uses Java's SecureRandom.
On Linux it is known to exhaust the entropy pool, hanging up the process.

This one is much faster, still has pretty good security characteristics and never hangs on Linux's entropy pool.

# Epoch UUID
Uses MAC addresses, a random number, timestamp and a counter, runs it through SHA1 and gets 12 bytes from the resulting hash. 

Finally it prepends a MSB first timestamp in seconds since the epoch. It's unsigned and will wrap in 2106.

# Speed
random: about 10e6/s

epoch: about 100e3/s

on a 2.4GHz Core i5.

# See also
[Java UUID Generator](https://github.com/cowtowncoder/java-uuid-generator).
