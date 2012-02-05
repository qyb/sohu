# encoding: utf-8
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models

class Migration(SchemaMigration):

    def forwards(self, orm):
        
        # Adding model 'MyImageInstance'
        db.create_table('image_myimageinstance', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user_id', self.gf('django.db.models.fields.IntegerField')()),
            ('key', self.gf('django.db.models.fields.CharField')(max_length=256)),
            ('url', self.gf('django.db.models.fields.URLField')(max_length=200)),
            ('myarticle_instance_id', self.gf('django.db.models.fields.IntegerField')()),
            ('title', self.gf('django.db.models.fields.CharField')(max_length=256, blank=True)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=1024, blank=True)),
            ('width', self.gf('django.db.models.fields.IntegerField')(null=True, blank=True)),
            ('height', self.gf('django.db.models.fields.IntegerField')(null=True, blank=True)),
        ))
        db.send_create_signal('image', ['MyImageInstance'])


    def backwards(self, orm):
        
        # Deleting model 'MyImageInstance'
        db.delete_table('image_myimageinstance')


    models = {
        'image.myimageinstance': {
            'Meta': {'object_name': 'MyImageInstance'},
            'description': ('django.db.models.fields.CharField', [], {'max_length': '1024', 'blank': 'True'}),
            'height': ('django.db.models.fields.IntegerField', [], {'null': 'True', 'blank': 'True'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'key': ('django.db.models.fields.CharField', [], {'max_length': '256'}),
            'myarticle_instance_id': ('django.db.models.fields.IntegerField', [], {}),
            'title': ('django.db.models.fields.CharField', [], {'max_length': '256', 'blank': 'True'}),
            'url': ('django.db.models.fields.URLField', [], {'max_length': '200'}),
            'user_id': ('django.db.models.fields.IntegerField', [], {}),
            'width': ('django.db.models.fields.IntegerField', [], {'null': 'True', 'blank': 'True'})
        }
    }

    complete_apps = ['image']
