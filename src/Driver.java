//import BLL.EditorBO;
//import BLL.FacadeBO;
//import BLL.IFacadeBO;
//import DAL.AbstractDAOEditorFactory;
//import DAL.FacadeDAO;
//import DAL.IEditorDBDAO;
//import DAL.IFacadeDAO;
//import PL.EditorPO;
//
//public class Driver {
//
//	public Driver() {
//    }
//
//    public static void main(String[] args) {
//
//    	IEditorDBDAO editorDAO = AbstractDAOEditorFactory.getInstance().createEditorDAO();
//        IFacadeDAO facadeDAO = new FacadeDAO(editorDAO);
//
//        IFacadeBO editorBO = new FacadeBO(new EditorBO(facadeDAO));
//
//        EditorPO po = new EditorPO(editorBO);
//    }
//}