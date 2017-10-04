--Add admin user
INSERT INTO "user" (id, name, openid, password, fullname, email, apikey, created, reset_key, about, activity_streams_email_notifications, sysadmin, state)
VALUES ('401139db-8f3e-4371-8233-5d51d4c4c8b6', 'admin', NULL, '$pbkdf2-sha512$25000$dq51TgmB0JpzDiGEkDKGUA$YVW2kq0qZrp7ZMge7P33JCTTaUaz9eH0eKXDNENRpOJ/ndCrpBGR94Qm3XakaGBLCA54qi1Q6wxnDgcfvgOxbA', 'Admin Adminson', 'admin@napoteadmin123.com', 'd7c6dccf-6541-4443-a9b4-7ab7c36735bc', '2017-10-04T09:45:10.895301'::timestamp, NULL, NULL, false, true, 'active');
INSERT INTO "activity" (id, timestamp, user_id, object_id, revision_id, activity_type, data)
VALUES ('2fc23860-6275-4712-a1a8-37bab21cc2b6', '2017-10-04T09:45:10.899229'::timestamp, '401139db-8f3e-4371-8233-5d51d4c4c8b6', '401139db-8f3e-4371-8233-5d51d4c4c8b6', NULL, 'new user', NULL);
INSERT INTO "dashboard" (user_id, activity_stream_last_viewed, email_last_sent)
VALUES ('401139db-8f3e-4371-8233-5d51d4c4c8b6', '2017-10-04T09:45:10.916047'::timestamp, '2017-10-04T09:45:10.916060'::timestamp);
