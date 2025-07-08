use std::path::Path;

use crate::fs::FileSystem;
use crate::fs::FileSystemError;
use crate::fs::DirEntry;

#[derive(Default)]
pub struct LocalFileSystem;

impl FileSystem for LocalFileSystem {
    fn list_dir(&self, path: &Path) -> Result<Vec<DirEntry>, FileSystemError> {
        let mut entries = Vec::new();
        
        Ok(entries)
    }
}