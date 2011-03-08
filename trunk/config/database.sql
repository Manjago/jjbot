CREATE TABLE [Log] (
  [Id] integer NOT NULL PRIMARY KEY AUTOINCREMENT, 
  [Jid] VARCHAR2(1000) NOT NULL, 
  [From] VARCHAR2(1000) NOT NULL, 
  [Message] VARCHAR2(1000) NOT NULL, 
  [Date] TIMESTAMP NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f', 'now')), 
  [Type] char(1) NOT NULL DEFAULT ('N'));

CREATE INDEX [log_jid_date] ON [Log] ([Jid], [Date]);

