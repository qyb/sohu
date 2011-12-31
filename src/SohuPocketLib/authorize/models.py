from django.db import models

# Create your models here.
class User(models.Model):
    sohupassport_uid = models.CharField(max_length = 128)
    sohupassport_userid = models.CharField(max_length = 512)
    sohupassport_uuid = models.CharField(max_length = 128)
    # unique and secret access key for kan.sohu.com
    kan_certification = models.CharField(max_length = 256)
    # different from the automatic generated id, only to hide the real user group size
    kan_uid = models.CharField(max_length = 128)
    # user modifiable username
    kan_username = models.CharField(max_length = 256)
    # more to be complemented