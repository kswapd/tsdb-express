CREATE DATABASE "metrics_db3" WITH DURATION 3h REPLICATION 1 NAME "rp_3h"

CREATE RETENTION POLICY "rp_1y" ON "metrics_db3" DURATION 52w REPLICATION 1;

CREATE RETENTION POLICY "rp_3month" ON "metrics_db3" DURATION 90d REPLICATION 1;

CREATE RETENTION POLICY "rp_1month" ON "metrics_db3" DURATION 30d REPLICATION 1;

CREATE RETENTION POLICY "rp_1d" ON "metrics_db3" DURATION 1d REPLICATION 1;




CREATE CONTINUOUS QUERY "cq_to_1d_int2m" ON "metrics_db3" BEGIN SELECT mean("percent") as "percent"  INTO "metrics_db3"."rp_1d"."memory_mean_2m"  FROM "memory" GROUP BY time(2m), * END

CREATE CONTINUOUS QUERY "cq_to_1month_int1h" ON "metrics_db3" BEGIN SELECT mean("percent") as "percent"  INTO "metrics_db3"."rp_1month"."memory_mean_1h"  FROM "memory" GROUP BY time(1h), * END

CREATE CONTINUOUS QUERY "cq_to_1y_int1d" ON "metrics_db3" BEGIN SELECT mean("percent") as "percent"  INTO "metrics_db3"."rp_1y"."memory_mean_1d"  FROM "memory" GROUP BY time(1d), * END
