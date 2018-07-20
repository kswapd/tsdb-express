CREATE DATABASE "metrics_db3" WITH DURATION 3h REPLICATION 1 NAME "rp_3h"

CREATE RETENTION POLICY "rp_1y" ON "metrics_db3" DURATION 52w REPLICATION 1;

CREATE RETENTION POLICY "rp_3month" ON "metrics_db3" DURATION 90d REPLICATION 1;

CREATE RETENTION POLICY "rp_1month" ON "metrics_db3" DURATION 30d REPLICATION 1;

CREATE RETENTION POLICY "rp_1d" ON "metrics_db3" DURATION 1d REPLICATION 1;




CREATE CONTINUOUS QUERY "cq_to_1d_int2m" ON "metrics_db3" BEGIN SELECT mean("percent") as "percent"  INTO "metrics_db3"."rp_1d"."memory_mean_2m"  FROM "memory" GROUP BY time(2m), * END

CREATE CONTINUOUS QUERY "cq_to_1month_int1h" ON "metrics_db3" BEGIN SELECT mean("percent") as "percent"  INTO "metrics_db3"."rp_1month"."memory_mean_1h"  FROM "memory" GROUP BY time(1h), * END

CREATE CONTINUOUS QUERY "cq_to_1y_int1d" ON "metrics_db3" BEGIN SELECT mean("percent") as "percent"  INTO "metrics_db3"."rp_1y"."memory_mean_1d"  FROM "memory" GROUP BY time(1d), * END




CREATE DATABASE "metrics_oms" WITH DURATION 30d REPLICATION 1 NAME "rp_30d"
CREATE RETENTION POLICY "rp_1y" ON "metrics_oms" DURATION 365d REPLICATION 1;
CREATE CONTINUOUS QUERY "cq_to_1y_int10m" ON "metrics_oms" BEGIN SELECT mean("percent") as "percent"  INTO "metrics_oms"."rp_1y"."memory"  FROM "rp_30d"."memory" GROUP BY time(10m), * END



CREATE RETENTION POLICY "rp_10m" ON "metrics_oms" DURATION 10m REPLICATION 1;
CREATE CONTINUOUS QUERY "cq_to_1y_int5m" ON "metrics_oms" BEGIN SELECT count("value") as "count_success"  INTO "metrics_oms"."rp_1y"."trade"  FROM "rp_10m"."trade" where ret_code="000000" GROUP BY time(5m),source_type  END
CREATE CONTINUOUS QUERY "cq_to_1y_int5m" ON "metrics_oms" BEGIN SELECT count("value") as "count_fail_1"  INTO "metrics_oms"."rp_1y"."trade"  FROM "rp_10m"."trade" where ret_code="000000" GROUP BY time(5m),source_type  END








