-- KEYS[1] = tokens key
-- KEYS[2] = last refill timestamp key
-- ARGV[1] = capacity
-- ARGV[2] = refill_rate (tokens/sec)
-- ARGV[3] = current time (in seconds)

local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local tokens = tonumber(redis.call("GET", tokens_key) or capacity)
local last_refill = tonumber(redis.call("GET", timestamp_key) or now)

local delta = now - last_refill
local refill = delta * refill_rate
tokens = math.min(capacity, tokens + refill)

if tokens < 1 then
    return 0
end

tokens = tokens - 1
redis.call("SET", tokens_key, tokens)
redis.call("SET", timestamp_key, now)

return 1