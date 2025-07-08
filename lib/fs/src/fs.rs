use std::path::Path;
use std::path::PathBuf;

pub trait FileSystem {
    fn list_dir(&self, path: &Path) -> Result<Vec<DirEntry>, FileSystemError>;
}

#[derive(Debug)]
pub struct DirEntry {
    pub path: PathBuf,
    pub is_file: bool,
}

pub enum FileSystemError {
}