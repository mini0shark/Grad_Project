from storages.backends.s3boto3 import S3Boto3Storage

class MediaStorage(S3Boto3Storage):
	location = 'media' # s3 저장경로
	file_overwrite = False # 이름 그대로
