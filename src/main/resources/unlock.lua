if(redis.call('get', KEY[1]) == ARGV[1]) then
    return redis.call('del', KEYS[1])
end
return 0
