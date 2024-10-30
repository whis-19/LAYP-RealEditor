package DAL;

public class MariaDBDAOFactory extends AbstractDAOEditorFactory{

	@Override
	public IEditorDBDAO createEditorDAO() {
		return new EditorDBDAO();
	}
	
}