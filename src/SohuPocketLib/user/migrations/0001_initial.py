# encoding: utf-8
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models

class Migration(SchemaMigration):

    def forwards(self, orm):
        
        # Adding model 'User'
        db.create_table('user_user', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('sohupassport_uuid', self.gf('django.db.models.fields.CharField')(max_length=128)),
            ('kan_username', self.gf('django.db.models.fields.CharField')(max_length=256, blank=True)),
            ('kan_self_description', self.gf('django.db.models.fields.CharField')(max_length=1024, blank=True)),
        ))
        db.send_create_signal('user', ['User'])

        # Adding model 'Access'
        db.create_table('user_access', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('access_token', self.gf('django.db.models.fields.CharField')(max_length=256)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['user.User'])),
        ))
        db.send_create_signal('user', ['Access'])


    def backwards(self, orm):
        
        # Deleting model 'User'
        db.delete_table('user_user')

        # Deleting model 'Access'
        db.delete_table('user_access')


    models = {
        'user.access': {
            'Meta': {'object_name': 'Access'},
            'access_token': ('django.db.models.fields.CharField', [], {'max_length': '256'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': "orm['user.User']"})
        },
        'user.user': {
            'Meta': {'object_name': 'User'},
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'kan_self_description': ('django.db.models.fields.CharField', [], {'max_length': '1024', 'blank': 'True'}),
            'kan_username': ('django.db.models.fields.CharField', [], {'max_length': '256', 'blank': 'True'}),
            'sohupassport_uuid': ('django.db.models.fields.CharField', [], {'max_length': '128'})
        }
    }

    complete_apps = ['user']
