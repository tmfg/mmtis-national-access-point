-- ### Add admin user ###
INSERT INTO "user" (id, name, openid, password, fullname, email, apikey, created, reset_key, about, activity_streams_email_notifications, sysadmin, state, "email-confirmed?")
VALUES ('401139db-8f3e-4371-8233-5d51d4c4c8b6', 'admin', NULL,
                                                '$pbkdf2-sha512$25000$dq51TgmB0JpzDiGEkDKGUA$YVW2kq0qZrp7ZMge7P33JCTTaUaz9eH0eKXDNENRpOJ/ndCrpBGR94Qm3XakaGBLCA54qi1Q6wxnDgcfvgOxbA',
                                                'Admin Adminson', 'admin@napoteadmin123.com',
                                                'd7c6dccf-6541-4443-a9b4-7ab7c36735bc',
                                                '2017-10-04T09:45:10.895301' :: TIMESTAMP, NULL, NULL, FALSE, TRUE, 'active', TRUE);
INSERT INTO "activity" (id, timestamp, user_id, object_id, revision_id, activity_type, data)
VALUES ('2fc23860-6275-4712-a1a8-37bab21cc2b6', '2017-10-04T09:45:10.899229' :: TIMESTAMP,
        '401139db-8f3e-4371-8233-5d51d4c4c8b6', '401139db-8f3e-4371-8233-5d51d4c4c8b6', NULL, 'new user', NULL);
INSERT INTO "dashboard" (user_id, activity_stream_last_viewed, email_last_sent)
VALUES ('401139db-8f3e-4371-8233-5d51d4c4c8b6', '2017-10-04T09:45:10.916047' :: TIMESTAMP,
        '2017-10-04T09:45:10.916060' :: TIMESTAMP);


-- ### Add a test organization for the admin user ###
INSERT INTO revision (id, timestamp, author, message, state, approved_timestamp)
VALUES ('165052ab-2852-4ecd-8c17-408d36ef4826', '2017-10-04T14:19:50.231620' :: TIMESTAMP, NULL, NULL, 'active', NULL);

UPDATE revision
SET author = 'admin', message = ''
WHERE revision.id = '165052ab-2852-4ecd-8c17-408d36ef4826';

INSERT INTO "group" (id, name, title, type, description, image_url, created, is_organization, approval_status, state, revision_id)
VALUES ('79046442-ad25-4865-a174-ec199a4b39c4', 'taksiyritys-testinen-oy', 'Taksiyritys Testinen Oy', 'organization',
                                                'Testiorganisaatio.', '', '2017-10-04T14:19:50.290361' :: TIMESTAMP,
                                                TRUE, 'approved', 'active', '165052ab-2852-4ecd-8c17-408d36ef4826');

INSERT INTO group_revision (id, name, title, type, description, image_url, created, is_organization, approval_status, state, revision_id, continuity_id, expired_timestamp)
VALUES ('79046442-ad25-4865-a174-ec199a4b39c4', 'taksiyritys-testinen-oy', 'Taksiyritys Testinen Oy', 'organization',
                                                'Testiorganisaatio.', '', '2017-10-04T14:19:50.290361' :: TIMESTAMP,
                                                TRUE, 'approved', 'active', '165052ab-2852-4ecd-8c17-408d36ef4826',
        '79046442-ad25-4865-a174-ec199a4b39c4', '9999-12-31T00:00:00' :: TIMESTAMP);

INSERT INTO member (id, table_name, table_id, capacity, group_id, state, revision_id)
VALUES ('fdab3ed2-7e3d-42f9-8084-a0c0452ee8ba', 'user', '401139db-8f3e-4371-8233-5d51d4c4c8b6', 'admin',
        '79046442-ad25-4865-a174-ec199a4b39c4', 'active', '165052ab-2852-4ecd-8c17-408d36ef4826');

INSERT INTO member_revision (id, table_name, table_id, capacity, group_id, state, revision_id, continuity_id, expired_timestamp)
VALUES ('fdab3ed2-7e3d-42f9-8084-a0c0452ee8ba', 'user', '401139db-8f3e-4371-8233-5d51d4c4c8b6', 'admin',
        '79046442-ad25-4865-a174-ec199a4b39c4', 'active', '165052ab-2852-4ecd-8c17-408d36ef4826',
        'fdab3ed2-7e3d-42f9-8084-a0c0452ee8ba', '9999-12-31T00:00:00' :: TIMESTAMP);

INSERT INTO activity (id, timestamp, user_id, object_id, revision_id, activity_type, data)
VALUES ('7684ea02-4310-4250-b9e5-2d2bfafb7ad2', '2017-10-04T14:19:50.333249' :: TIMESTAMP,
        '401139db-8f3e-4371-8233-5d51d4c4c8b6', '79046442-ad25-4865-a174-ec199a4b39c4',
        '165052ab-2852-4ecd-8c17-408d36ef4826', 'new organization',
        '{"group": {"description": "Testiorganisaatio.", "title": "Taksiyritys Testinen Oy", "created": "2017-10-04T14:19:50.290361", "approval_status": "approved", "is_organization": true, "state": "active", "image_url": "", "revision_id": "165052ab-2852-4ecd-8c17-408d36ef4826", "type": "organization", "id": "79046442-ad25-4865-a174-ec199a4b39c4", "name": "taksiyritys-testinen-oy"}}');

UPDATE member_revision
SET current = '0'
WHERE member_revision.id = 'fdab3ed2-7e3d-42f9-8084-a0c0452ee8ba' AND member_revision.current = '1';

UPDATE member_revision
SET revision_timestamp = '2017-10-04T14:19:50.231620' :: TIMESTAMP
WHERE member_revision.id = 'fdab3ed2-7e3d-42f9-8084-a0c0452ee8ba' AND
      member_revision.revision_id = '165052ab-2852-4ecd-8c17-408d36ef4826';

UPDATE group_revision
SET current = '0'
WHERE group_revision.id = '79046442-ad25-4865-a174-ec199a4b39c4' AND group_revision.current = '1';

UPDATE group_revision
SET revision_timestamp = '2017-10-04T14:19:50.231620' :: TIMESTAMP
WHERE group_revision.id = '79046442-ad25-4865-a174-ec199a4b39c4' AND
      group_revision.revision_id = '165052ab-2852-4ecd-8c17-408d36ef4826';

INSERT INTO revision (id, timestamp, author, message, state, approved_timestamp)
VALUES ('7b7f56df-1c88-48b7-b807-6bcfaedb274a', '2017-10-04T14:19:50.389506' :: TIMESTAMP, NULL, NULL, 'active', NULL);

UPDATE revision
SET author = 'admin', message = 'REST API: Luo jäsenobjekti '
WHERE revision.id = '7b7f56df-1c88-48b7-b807-6bcfaedb274a';


-- ### ... ###

-- Add regular user (normaluser/password)
INSERT INTO "public"."user"("id","name","apikey","created","about","openid","password","fullname","email","reset_key","sysadmin","activity_streams_email_notifications","state", "email-confirmed?")
VALUES
(E'676a2532-b106-4329-b95e-e30c8c8265d5',E'normaluser',E'8eb7bf65-2a7b-45dd-a576-be10a02c3801',E'2018-01-17 11:46:11.658956',NULL,NULL,E'$pbkdf2-sha512$25000$UApBKOXcW6uVci4FQKiVcg$B4j1WY60oVyXvJHA9YRXIN8wKl8lD.Gzn802IwOLTuCgcIqbaaEQIJcqaIWr2ROf4XBtKdCTSCyroLhIMpFC9g',E'User Userson',E'user.userson@example.com',NULL,FALSE,FALSE,E'active',TRUE);

INSERT INTO "public"."revision"("id","timestamp","author","message","state","approved_timestamp")
VALUES
(E'8f1410fe-5a9d-4a96-be5a-3f8bc4273ff4',E'2018-01-23 08:38:51.32146',E'normaluser',E'REST API: Luo jäsenobjekti ',E'active',NULL);

INSERT INTO "public"."revision"("id","timestamp","author","message","state","approved_timestamp")
VALUES
(E'9d7176c9-df56-492d-b243-f4ca762d014f',E'2018-01-23 08:38:51.286964',E'normaluser',E'REST API: Luo objekti transport-operator-2',E'active',NULL);

INSERT INTO "public"."group"("id","name","title","description","created","state","revision_id","type","approval_status","image_url","is_organization")
VALUES
(E'ff5ca54d-2ff5-476d-9ad4-e903b6d1eeb4',E'transport-operator-2',E'Normaali yritys Oy',E'',E'2018-01-23 08:26:55.597997',E'active',E'9d7176c9-df56-492d-b243-f4ca762d014f',E'organization',E'approved',E'',TRUE);

INSERT INTO "public"."group_revision"("id","name","title","description","created","state","revision_id","continuity_id","expired_id","revision_timestamp","expired_timestamp","current","type","approval_status","image_url","is_organization")
VALUES
(E'ff5ca54d-2ff5-476d-9ad4-e903b6d1eeb4',E'transport-operator-2',E'Terminaali Oy',E'',E'2018-01-23 08:38:51.300609',E'active',E'9d7176c9-df56-492d-b243-f4ca762d014f',E'ff5ca54d-2ff5-476d-9ad4-e903b6d1eeb4',NULL,E'2018-01-23 08:38:51.286964',E'9999-12-31 00:00:00',NULL,E'organization',E'approved',E'',TRUE);

INSERT INTO "public"."member"("id","table_id","group_id","state","revision_id","table_name","capacity")
VALUES
(E'76d4d130-5167-448c-9c6b-c8f897993383',E'676a2532-b106-4329-b95e-e30c8c8265d5',E'ff5ca54d-2ff5-476d-9ad4-e903b6d1eeb4',E'active',E'9d7176c9-df56-492d-b243-f4ca762d014f',E'user',E'admin');

INSERT INTO "public"."member_revision"("id","table_id","group_id","state","revision_id","continuity_id","expired_id","revision_timestamp","expired_timestamp","current","table_name","capacity")
VALUES
(E'76d4d130-5167-448c-9c6b-c8f897993383',E'676a2532-b106-4329-b95e-e30c8c8265d5',E'ff5ca54d-2ff5-476d-9ad4-e903b6d1eeb4',E'active',E'9d7176c9-df56-492d-b243-f4ca762d014f',E'76d4d130-5167-448c-9c6b-c8f897993383',NULL,E'2018-01-23 08:38:51.32146',E'9999-12-31 00:00:00',NULL,E'user',E'admin');

-- Make our test operator a "transport authority" for easier testing
INSERT INTO "public"."group_extra"("id","group_id","key","value","state","revision_id")
VALUES
(E'ely-group-extra',E'79046442-ad25-4865-a174-ec199a4b39c4',E'transit-authority?',E'true',NULL,NULL);
